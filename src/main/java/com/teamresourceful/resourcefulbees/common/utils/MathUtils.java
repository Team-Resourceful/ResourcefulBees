package com.teamresourceful.resourcefulbees.common.utils;

public final class MathUtils {

    private MathUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean inRangeInclusive(int value, int min, int max) {
        return value <= max && value >= min;
    }

    public static final double HALF_PI = Math.PI/2;
}
