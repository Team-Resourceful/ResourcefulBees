package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;

import java.util.List;

public final class BreederConstants {

    private BreederConstants() {
        throw new UtilityClassError();
    }

    public static final int NUM_OF_BREEDERS = 2;

    public static final List<Integer> PARENT_1_SLOTS = List.of(1,6);
    public static final List<Integer> FEED_1_SLOTS = List.of(2,7);
    public static final List<Integer> PARENT_2_SLOTS = List.of(3,8);
    public static final List<Integer> FEED_2_SLOTS = List.of(4,9);
    public static final List<Integer> EMPTY_JAR_SLOTS = List.of(5,10);
    public static final int[] SLOTS = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28};
    public static final int DEFAULT_BREEDER_TIME = 2400;

}
