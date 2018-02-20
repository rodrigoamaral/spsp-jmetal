package net.rodrigoamaral.dspsp.decision;

import java.util.Arrays;

/**
 * Represents the comparison matrix used in decision making procedure
 * to choose the preferred schedule to use after rescheduling happens.
 *
 * Indexing starts in 1 due to readability and compatibility issues with
 * original MATLAB code and mathematical notation.
 */
public class ComparisonMatrix {
    private double[][] cm = new double[4][4];

    public ComparisonMatrix() {
        set(1, 1, 1);
        set(1, 2, 1);
        set(1, 3, 2);
        set(1, 4, 2);
        set(2, 1, 1 / get(1, 2));
        set(2, 2, 1);
        set(2, 3, 2);
        set(2, 4, 2);
        set(3, 1, 1 / get(1, 3));
        set(3, 2, 1 / get(2, 3));
        set(3, 3, 1);
        set(3, 4, 1);
        set(4, 1, 1 / get(1, 4));
        set(4, 2, 1 / get(2, 4));
        set(4, 3, 1 / get(3, 4));
        set(4, 4, 1);
    }

    private void set(int r, int c, double value) {
        cm[r-1][c-1] = value;
    }

    public double get(int r, int c) {
        return cm[r-1][c-1];
    }

    public double[] getRow(int r) {
        return cm[r-1];
    }

    /**
     * Calculates weight vector for decision making
     * of the fisrt schedule.
     *
     * @return weight vector used in decision making
     */
    public double[] initialWeights() {
        double[] weights = new double[3];
        double sum = 0;
        double[][] im = initialMatrix();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = geometricMean(im[i]);
            sum += weights[i];
        }
        // Normalizing weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }

    private double[][] initialMatrix() {
        double[][] im = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                im[i][j] = cm[i][j];
            }
        }
        return im;
    }

    /**
     * Calculates weight vector for decision making
     * of the reschedulings.
     *
     * @return weight vector used in decision making
     */
    public double[] reschedulingWeights() {
        double[] weights = new double[4];
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            weights[i] = geometricMean(cm[i]);
            sum += weights[i];
        }
        // Normalizing weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }

    private double geometricMean(double[] values) {
        double prod = 1;
        for (int i = 0; i < values.length; i++) {
            prod *= values[i];
        }
        return Math.pow(prod, 1.0 / values.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComparisonMatrix{cm=[");
        for (int i = 1; i <= 4 ; i++) {
            sb.append(Arrays.toString(this.getRow(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

}
