package ch.fhnw.M15_649_411.efalg.nonogram.algorithms.zx_algorithm;

import static ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard.State;

/**
 * created on 01.10.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class Simple {
    /**
     * Determines the overlap of a line
     * @param length length of line
     * @param vectors vector lengths of the line
     * @return overlap
     */
    public State[] overlap(int length, int[] vectors) {
        State[] overlap = State.createArray(length, State.UNDEFINED);
        //iterate over each row
        int sum = vectors[0];
        for (int j = 1; j < vectors.length; j++) {
            sum += 1 + vectors[j];
        }
        int a = length - sum;
        int start = 0;
        if (sum == 0) {
            //empty line
            for (int j = 0; j < length; j++) {
                overlap[j] = State.WHITE;
            }
        } else if (a == 0) {
            //complete line
            for (int p = 0; p < vectors[0]; p++) {
                overlap[start + p] = State.BLACK;
            }
            overlap[start + a + vectors[0]] = State.BLACK;
            start += vectors[0] + 1;
            for (int j = 1; j < vectors.length; j++) {
                overlap[start - 1] = State.BLACK;
                for (int p = 0; p < vectors[j] - a; p++) {
                    overlap[start + a + p] = State.BLACK;
                }
                start += vectors[j] + 1;
            }
        } else {
            //part of line
            for (int vector : vectors) {
                if (vector > a) {
                    for (int p = 0; p < vector - a; p++) {
                        overlap[start + a + p] = State.BLACK;
                    }
                }
                start += vector + 1;
            }
        }
        return overlap;
    }
}
