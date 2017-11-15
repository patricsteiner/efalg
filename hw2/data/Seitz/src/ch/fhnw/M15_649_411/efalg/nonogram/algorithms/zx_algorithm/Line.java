package ch.fhnw.M15_649_411.efalg.nonogram.algorithms.zx_algorithm;

import ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard;

import java.util.Arrays;

//Line data structure with priority field
/**
 * created on 07.10.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class Line {
    public enum Layout {
        ROW,
        COLUMN
    }

    private static double H_VEC_LEBGTH_EXP = 2;

    public final Layout layout;
    public final int index;
    public final int length;
    public final int[] vectors;

    private double prio; //priority
    private int placedWhite, placedBlack;
    private int vecSum;
    private boolean mod;

    public Line(Layout layout, int index, int length, int[] vectors) {
        this.layout = layout;
        this.index = index;
        this.length = length;
        this.vectors = vectors;
        this.vecSum = Arrays.stream(vectors).sum();
    }

    /**
     * Places a new state on this line
     * @param oldState previous state
     * @param newState new state, newState != UNDEFINED
     */
    public void setPlaced(NonoBoard.State oldState, NonoBoard.State newState) {
        if(newState == NonoBoard.State.BLACK) {
            placedBlack++;
        } else {
            placedWhite++;
        }
        // Remember modification to calculate new ordering, not calculating every placing step
        this.mod = true;
    }

    public boolean isComplete() {
        return getPlaced() == length;
    }

    public int getPlaced() {
        return placedWhite + placedBlack;
    }

    public int getPlacedBlack() {
        return placedBlack;
    }

    public int getPlacedWhite() {
        return placedWhite;
    }

    /**
     * Returns the priority to reorder the heap
     * @return priority
     */
    public double getPrio() {
        if (mod) {
            this.prio = heuristic();
            mod = false;
        }

        return prio;
    }

    /**
     * Heuristic function
     * @return new priority
     */
    private double heuristic() {
        return ((length - getPlaced()))
                * (1. / ((vecSum / (double)vectors.length)))
                * ((vecSum - placedBlack))
                * (Math.pow(vectors.length, H_VEC_LEBGTH_EXP));
    }

    public boolean isModPrio() {
        return mod;
    }

    @Override
    public String toString() {
        return layout + ": " + index;
    }
}
