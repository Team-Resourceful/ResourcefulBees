package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

import java.util.Random;

public final class MathUtils {

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
}
