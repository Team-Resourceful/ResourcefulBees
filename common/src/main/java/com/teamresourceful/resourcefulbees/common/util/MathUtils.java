package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

import java.util.Random;

public final class MathUtils {

    private static final String[] UNITS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    private static final String[] TENS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] HUNDREADS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] THOUSANDS = {"", "M", "MM", "MMM"};

    private MathUtils() {
        throw new UtilityClassError();
    }

    public static boolean inRangeInclusive(int value, int min, int max) {
        return value <= max && value >= min;
    }

    public static final double HALF_PI = Math.PI/2;

    //exists so I can reduce level != null checks and access a random even if a level value isn't accessible
    // seriously why doesn't mojang just have this in a math util class instead of level??
    public static final Random RANDOM = new Random();

    public static String createRomanNumeral(int value) {
        return THOUSANDS[value / 1000] + HUNDREADS[(value % 1000) / 100] + TENS[(value % 100) / 10] + UNITS[value % 10];
    }
}
