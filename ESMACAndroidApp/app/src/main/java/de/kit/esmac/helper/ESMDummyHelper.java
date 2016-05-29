package de.kit.esmac.helper;

/**
 * Created by Robert on 12.02.2015.
 */
public class ESMDummyHelper {
    public static boolean getBooleanForRestrictedOperator(String value, String result, String operator) {
        if (operator.equals("=")) {
            return value.equals(result);
        } else if (operator.equals("!=")) {
            return !value.equals(result);
        }
        return false;
    }

    public static boolean getBooleanForFullOperator(Integer bound, Integer result, String operator) {
        if (operator.equals("=")) {
            return bound == result;
        } else if (operator.equals("!=")) {
            return !(bound == result);
        } else if (operator.equals(">")) {
            return (bound < result);
        } else if (operator.equals("<")) {
            return (bound > result);
        } else if (operator.equals(">=")) {
            return (bound <= result);
        } else if (operator.equals("<=")) {
            return (bound >= result);
        }
        return false;
    }

    public static boolean getBooleanForFullOperator(Double value, Double result, String operator) {
        if (operator.equals("=")) {
            return value == result;
        } else if (operator.equals("!=")) {
            return !(value == result);
        } else if (operator.equals(">")) {
            return (value < result);
        } else if (operator.equals("<")) {
            return (value > result);
        } else if (operator.equals(">=")) {
            return (value <= result);
        } else if (operator.equals("<=")) {
            return (value >= result);
        }
        return false;
    }

    public static boolean getBooleanForFullOperator(Float value, Float result, String operator) {
        if (operator.equals("=")) {
            return value == result;
        } else if (operator.equals("!=")) {
            return !(value == result);
        } else if (operator.equals(">")) {
            return (value < result);
        } else if (operator.equals("<")) {
            return (value > result);
        } else if (operator.equals(">=")) {
            return (value <= result);
        } else if (operator.equals("<=")) {
            return (value >= result);
        }
        return false;
    }

}
