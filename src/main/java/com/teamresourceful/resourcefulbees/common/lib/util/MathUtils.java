package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class MathUtils {

    private static final String[] UNITS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    private static final String[] TENS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] HUNDREDS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] THOUSANDS = {"", "M", "MM", "MMM"};

    private MathUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static boolean inRangeInclusive(int value, int min, int max) {
        return value <= max && value >= min;
    }

    public static boolean inRangeInclusive(double value, double min, double max) {
        return value <= max && value >= min;
    }

    public static final double HALF_PI = Math.PI/2;

    public static String createRomanNumeral(int value) {
        return THOUSANDS[value / 1000] + HUNDREDS[(value % 1000) / 100] + TENS[(value % 100) / 10] + UNITS[value % 10];
    }
}
