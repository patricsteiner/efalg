package ch.fhnw.M15_649_411.efalg.nonogram.algorithms.zx_algorithm;

import java.util.Arrays;

import ch.fhnw.M15_649_411.efalg.nonogram.NonoBoard.State;

/**
 * created on 05.10.2017
 *
 * @author Claudio
 * @version 1.0
 */
public class Permutation {
    // Codes for Try class
    private static final int OK = 0;
    private static final int ABORT = -1;
    private static final int NEXT = 1;

    private class Try {
        public final int code;
        public final Object o;

        public Try(int code, Object o) {
            this.code = code;
            this.o = o;
        }
    }

    /**
     * Vector class to evaluate some arrangements
     */
    private class Vectors {
        private int[] v;
        final int sum;
        final int length;

        /**
         * Constructor
         * Checks and converts the black vectors into white-black-white vectors
         *
         * @param vectors    black vector lengths
         * @param lineLength length of line
         */
        Vectors(int[] vectors, int lineLength) {
            this.v = new int[vectors.length * 2 + 1];
            int vecSum = 0;
            for (int i = 0; i < vectors.length; i++) {
                v[i * 2 + 1] = vectors[i];
                vecSum += vectors[i];
            }
            for (int i = 1; i < vectors.length; i++)
                v[i * 2] = 1;
            vecSum += vectors.length - 1;
            if (vecSum > lineLength)
                throw new IllegalArgumentException("Sum of vectors is greater than length of subCondition");
            v[v.length - 1] = lineLength - vecSum;

            this.length = v.length;
            this.sum = lineLength;
        }

        int get(int i) {
            return v[i];
        }

        boolean isBlack(int i) {
            return (i & 1) == 1;
        }

        boolean isWhite(int i) {
            return (i & 1) == 0;
        }

        /**
         * Tries to extend the white vector p, on success set all white vectors after p to 1
         *
         * @param p      vector index
         * @param vector length to add
         * @return success
         */
        boolean modify(int p, int vector) {
            if (isBlack(p))
                return false;
            int[] nv = v.clone();
            nv[p] += vector;
            for (int q = p + 2; q < this.length - 2; q += 2)
                nv[q] = 1;
            nv[this.length - 1] = 0;
            int last = sum - Arrays.stream(nv).sum();
            if (last < 0)
                return false;
            nv[this.length - 1] = last;
            v = nv;
            return true;
        }
    }

    private State[] subCondition, condition;
    private int left, right;

    /**
     * Determines the overlap of a line dependent to a specified subCondition
     *
     * @param iVectors  black vector lengths
     * @param condition known line
     * @return overlap
     * @throws IllegalArgumentException incorrect vector / condition combination
     */
    public State[] overlap(int[] iVectors, State[] condition) throws IllegalArgumentException {
        this.left = 0;
        this.condition = condition;

        // lessen effect range
        //left side of condition
        State before = State.UNDEFINED;
        int ileft = 0, leftVec = 0;
        while (condition[ileft] != State.UNDEFINED) {
            if (condition[ileft] == State.WHITE) {
                this.left = ileft + 1;
                if (before == State.BLACK) leftVec++;
            }
            before = condition[ileft];
            ileft++;
        }
        //right side of condition
        before = State.UNDEFINED;
        this.right = condition.length - 1;
        int iright = condition.length - 1, rightVec = iVectors.length - 1;
        while (condition[iright] != State.UNDEFINED) {
            if (condition[iright] == State.WHITE) {
                this.right = iright - 1;
                if (before == State.BLACK) rightVec--;
            }
            before = condition[iright];
            iright--;
        }
        this.subCondition = Arrays.copyOfRange(condition, left, right + 1);

        //Special case: only white fields ind sub-condition left
        State[] overlap = null;
        if (leftVec > rightVec) {
            overlap = condition.clone();
            for (int i = left; i <= right; i++) {
                overlap[i] = State.WHITE;
            }
            return overlap;
        }

        // Sub-vectors dependent on sub-condition
        int[] subVectors = Arrays.copyOfRange(iVectors, leftVec, rightVec + 1);
        Vectors vectors = new Vectors(subVectors, this.subCondition.length);

        //Try first arrangement
        Try t = tryNext(vectors);
        if (t.code == OK) {
            overlap = (State[]) t.o;
        } else if (t.code == ABORT) {
            return null;
        }
        State[] arrangement;
        //try all arrangements
        int highestModify = vectors.length - 3;
        for (int p = highestModify; p >= 0; p -= 2) {
            while (vectors.modify(p, 1)) {
                //reset p after a lower p manipulation
                p = highestModify;
                //Try next arrangement
                t = tryNext(vectors);
                if (t.code == OK) {
                    arrangement = (State[]) t.o;
                    if (overlap != null) {
                        // determine overlap
                        for (int j = 0; j < condition.length; j++) {
                            if (overlap[j] != arrangement[j])
                                overlap[j] = State.UNDEFINED;
                        }
                    } else
                        overlap = arrangement;
                } else if (t.code == ABORT) {
                    return overlap;
                }
            }
        }
        return overlap;
    }

    /**
     * Tries to fill the line dependent to the specified sub-condition
     * Corrects itself if possible
     *
     * @param vectors current vectors state
     * @return possible arrangement
     */
    private Try tryNext(Vectors vectors) {
        State[] arrangement = condition.clone();
        int vector = vectors.get(0);
        if (!setFirstWhite(arrangement, vector))
            //No other arrangements possible
            return new Try(ABORT, null);
        int pos = vector;
        int i, p = 1;
        while (p < vectors.length) {
            vector = vectors.get(p);
            if (vectors.isBlack(p)) {
                // black vector
                i = setBlack(arrangement, pos, vector);
                if (i < 0) {
                    pos += vector;
                    p++;
                } else {
                    // try correct black set error
                    int j = setWhite(arrangement, pos, i + 1);
                    if (j < 0 && vectors.modify(p - 1, i + 1)) {
                        pos += i + 1;
                    } else {
                        // illegal correction
                        return new Try(NEXT, i);
                    }
                }
            } else {
                // white vector
                i = setWhite(arrangement, pos, vector);
                if (i < 0) {
                    pos += vector;
                    p++;
                } else {
                    return new Try(NEXT, i);
                }
            }
        }
        return new Try(OK, arrangement);
    }

    /**
     * Tries to set the first white vector
     *
     * @param arrangement current line to fill
     * @param length      length of first white vector
     * @return success
     */
    private boolean setFirstWhite(State[] arrangement, int length) {
        for (int i = 0; i < length; i++) {
            if (subCondition[i] == State.BLACK)
                return false;
            else
                arrangement[left + i] = State.WHITE;
        }
        return true;
    }

    /**
     * Tries to set a black vector, on error returns error position
     *
     * @param arrangement current line to fill
     * @param start       start position of black vector
     * @param length      length of black vector
     * @return -1 on success, otherwise error position in dependent of length
     */
    private int setBlack(State[] arrangement, int start, int length) {
        for (int i = 0; i < length; i++) {
            if (subCondition[start + i] == State.WHITE) {
                return i;
            } else {
                arrangement[left + start + i] = State.BLACK;
            }
        }
        return -1;
    }

    /**
     * Tries to set a white vector, on error returns error position
     *
     * @param arrangement current line to fill
     * @param start       start position of white vector
     * @param length      length of white vector
     * @return -1 on success, otherwise error position in dependent of length
     */
    private int setWhite(State[] arrangement, int start, int length) {
        for (int i = 0; i < length; i++) {
            if (subCondition[start + i] == State.BLACK) {
                return i;
            } else {
                arrangement[left + start + i] = State.WHITE;
            }
        }
        return -1;
    }
}
