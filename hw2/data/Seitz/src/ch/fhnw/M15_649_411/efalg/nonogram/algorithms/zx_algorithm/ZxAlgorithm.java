package ch.fhnw.M15_649_411.efalg.nonogram.algorithms.zx_algorithm;

import ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard;
import ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard.State;
import ch.fhnw.M15_649_411.efalg.nonogram.algorithms.AbstractAlgorithm;

import java.util.*;

/**
 * created on 05.10.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class ZxAlgorithm extends AbstractAlgorithm {
    private Simple so;
    private Permutation p;
    private Line[] rows, columns;
    private List<Line> maxHeap;
    private int boardCheck, maxFields;

    public ZxAlgorithm(NonoBoard board) {
        super(board);
        this.so = new Simple();
        this.p = new Permutation();
        this.rows = new Line[board.rows];
        for (int i = 0; i < board.rows; i++) {
            this.rows[i] = new Line(Line.Layout.ROW, i, board.columns, board.getxVecs()[i]);
        }
        this.columns = new Line[board.columns];
        for (int i = 0; i < board.columns; i++) {
            this.columns[i] = new Line(Line.Layout.COLUMN, i, board.rows, board.getyVecs()[i]);
        }
        this.maxHeap = new ArrayList<>();
        this.boardCheck = 0;
        this.maxFields = board.rows * board.columns;
    }

    @Override
    protected void execute() {
        simpleOverlap();
        if (!isSolution()) {
            permutation();
        }
    }

    /**
     * Determines the first overlaps
     */
    private void simpleOverlap() {
        // x dir
        for (int v = 0; v < board.rows; v++) {
            State[] overlap = so.overlap(board.columns, rows[v].vectors);
            for (int u = 0; u < board.columns; u++) {
                setField(Line.Layout.ROW, u, v, overlap[u]);
            }
        }

        // y dir
        for (int u = 0; u < board.columns; u++) {
            State[] overlap = so.overlap(board.rows, columns[u].vectors);
            for (int v = 0; v < board.rows; v++) {
                setField(Line.Layout.COLUMN, u, v, overlap[v]);
            }
        }
    }

    /**
     * Determines next overlaps while fields are set
     */
    private void permutation() {
        State[][] data = board.getData();
        //Sort list to get the best ordering to determine next overlaps
        maxHeap.sort(Comparator.comparingDouble(Line::getPrio).reversed());

        while (maxHeap.size() > 0) {
            Line line = maxHeap.remove(maxHeap.size() - 1);
            if (!line.isComplete()) {
                if (line.layout == Line.Layout.ROW) {
                    //x dir
                    int v = line.index;
//                    System.out.println("permutation row: " + v);
                    State[] condition = data[v];
                    State[] overlap2 = p.overlap(line.vectors, condition);

                    for (int u = 0; u < line.length; u++) {
                        setField(line.layout, u, v, overlap2[u]);
                    }
                } else {
                    //y dir
                    int u = line.index;
//                    System.out.println("permutation column: " + u);
                    State[] condition = new State[line.length];
                    for (int v = 0; v < line.length; v++) {
                        condition[v] = data[v][u];
                    }
                    State[] overlap2 = p.overlap(line.vectors, condition);

                    for (int v = 0; v < line.length; v++) {
                        setField(line.layout, u, v, overlap2[v]);
                    }
                }
            }
            maxHeap.sort(Comparator.comparingDouble(Line::getPrio).reversed());
        }
    }

    /**
     * Sets a field
     * @param layout line layout
     * @param u column index, x dir
     * @param v row index, y dir
     * @param state field
     */
    private void setField(Line.Layout layout, int u, int v, State state) {
        State field = board.getField(u, v);
        if (state != State.UNDEFINED && state != field) {
            board.setField(u, v, state);
            Line column = columns[u];
            column.setPlaced(field, state);
            Line row = rows[v];
            row.setPlaced(field, state);
            boardCheck++;

            // Add row or column to maxHeap if it's not in there, do not add to setting line
            if (layout == Line.Layout.ROW && !maxHeap.contains(column)) {
                maxHeap.add(column);
            } else if (layout == Line.Layout.COLUMN && !maxHeap.contains(row)) {
                maxHeap.add(row);
            }
        }
    }

    @Override
    protected boolean isSolution() {
        return boardCheck == maxFields;
    }
}
