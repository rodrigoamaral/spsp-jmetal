package net.rodrigoamaral.util;


import java.util.List;

public class DoubleUtils {
    public static double min(List<Double> values) {
        double result = Double.POSITIVE_INFINITY;
        for (double value : values) {
            if (value < result) {
                result = value;
            }
        }
        return result;
    }

    public static double max(List<Double> values) {
        double result = Double.NEGATIVE_INFINITY;
        for (double value : values) {
            if (value > result) {
                result = value;
            }
        }
        return result;
    }

    public static double sum(List<Double> doubleList) {
        double sum = 0;
        for (double d: doubleList) {
            sum += d;
        }
        return sum;
    }


}
