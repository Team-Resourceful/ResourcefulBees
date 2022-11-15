package com.teamresourceful.resourcefulbees.common.blockentity.breeder;

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

}
