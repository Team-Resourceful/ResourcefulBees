package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class BeeConstants {


    private BeeConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final String MOD_NAME = "Resourceful Bees";
    public static final String ENTITY_TEXTURES_DIR = "textures/entity/";
    public static final String STRING_DEFAULT_ITEM_COLOR = "-1";
    public static final String VANILLA_BEE_COLOR = "#edc343";
    public static final int VANILLA_BEE_INT_COLOR = 15582019;
    public static final Color DEFAULT_POLLEN_TOP_COLOR = new Color(13408304);
    public static final Color DEFAULT_POLLEN_BASE_COLOR = new Color(12681264);

    public static final double DEFAULT_BREED_WEIGHT = 10;
    public static final double DEFAULT_BREED_CHANCE = 1;

    public static final int SMOKE_TIME = 600;
    public static final int MIN_HIVE_TIME = 600;
    public static final int MAX_TIME_IN_HIVE = 2400;
    public static final int CHILD_GROWTH_DELAY = -24000;
    public static final int BREED_DELAY = 6000;

    public static final int DEFAULT_ITEM_COLOR = -1;
    public static final int MAX_BEES_BEE_BOX = 10;

    public static final int HONEY_PER_BOTTLE = 250;
    public static final double HONEY_PER_BOTTLE_BUCKETS = 0.25;
}
