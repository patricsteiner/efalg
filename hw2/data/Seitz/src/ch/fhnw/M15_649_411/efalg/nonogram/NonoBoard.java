package ch.fhnw.M15_649_411.efalg.nonogram;

import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * created on 25.09.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class NonoBoard {
    public interface Listener {
        void setField(int x, int y, State oldValue, State newValue);
    }

    /**
     * State of a field
     */
    public enum State {
        UNDEFINED(Color.GRAY),
        WHITE(Color.WHITE),
        BLACK(Color.BLACK);

        private Color color;

        State(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public static State[] createArray(int length, State state) {
            State[] array = new State[length];
            for (int i = 0; i < length; i++)
                array[i] = state;
            return array;
        }
    }

    private final List<Listener> listeners;
    public final int rows, columns, nxVecs, nyVecs;
    private final State[][] data;
    private final int[][] xVecs, yVecs;

    public NonoBoard(int[][] xVecs, int[][] yVecs) {
        this.listeners = new LinkedList<>();
        this.xVecs = xVecs.clone();
        this.yVecs = yVecs.clone();
        this.rows = xVecs.length;
        this.columns = yVecs.length;
        this.data = new State[rows][columns];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                this.data[y][x] = State.UNDEFINED;
            }
        }
        this.nxVecs = getMaxVectorLength(xVecs);
        this.nyVecs = getMaxVectorLength(yVecs);
    }

    public void resetBoard() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                setField(x, y, State.UNDEFINED);
            }
        }
    }

    /**
     * Returns the max length of the second dimension of an array
     *
     * @param array vectors array
     * @return max length
     */
    private int getMaxVectorLength(int[][] array) {
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].length > max) max = array[i].length;
        }
        return max;
    }

    public void addListener(Listener listener) {
        if (listener != null && !listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        if (listener != null && listeners.contains(listener)) listeners.remove(listener);
    }

    public State getField(int x, int y) {
        return this.data[y][x];
    }

    public void setField(int x, int y, State value) {
        //notify listeners
        for (Listener listener : listeners) listener.setField(x, y, getField(x, y), value);
        this.data[y][x] = value;
    }

    public State[][] getData() {
        return data.clone();
    }

    public int[][] getxVecs() {
        return xVecs.clone();
    }

    public int[][] getyVecs() {
        return yVecs.clone();
    }
}
