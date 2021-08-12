package com.resourcefulbees.resourcefulbees.lib;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.honeydata.DefaultHoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.config.Config;
import net.minecraft.util.ResourceLocation;

public class BeeConstants {

    private BeeConstants() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final String FLOWER_TAG_ALL = "all";
    public static final String FLOWER_TAG_SMALL = "small";
    public static final String FLOWER_TAG_TALL = "tall";
    public static final String TAG_PREFIX = "tag:";
    public static final String ENTITY_PREFIX = "entity:";
    public static final String MOD_NAME = "Resourceful Bees";
    public static final String ENTITY_TEXTURES_DIR = "textures/entity/";
    public static final String STRING_DEFAULT_ITEM_COLOR = "-1";
    public static final String INGREDIENT_COUNT = "count";
    public static final String VANILLA_BEE_TYPE = "minecraft";
    public static final String VANILLA_BEE_ID = "minecraft:bee";
    public static final String VANILLA_BEE_COLOR = "#edc343";
    public static final int VANILLA_BEE_INT_COLOR = 15582019;
    public static final String RAINBOW_COLOR = "rainbow";
    public static final String OREO_BEE = "oreo";
    public static final String KITTEN_BEE = "kitten";
    public static final String YETI_BEE = "yeti";

    public static final int SMOKE_TIME = 600;
    public static final int MIN_HIVE_TIME = 600;
    public static final int MAX_TIME_IN_HIVE = 2400;
    public static final double DEFAULT_BREED_WEIGHT = 10;
    public static final float DEFAULT_BREED_CHANCE = 1.0f;
    public static final float DEFAULT_MAIN_OUTPUT_WEIGHT = 1.0f;
    public static final float DEFAULT_SEC_OUTPUT_WEIGHT = 0.5f;
    public static final float DEFAULT_BOT_OUTPUT_WEIGHT = 0.25f;
    public static final float DEFAULT_FLUID_OUTPUT_WEIGHT = 1.0f;
    public static final int CHILD_GROWTH_DELAY = -24000;
    public static final int BREED_DELAY = 6000;

    public static final int DEFAULT_ITEM_COLOR = -1;
    public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/missing_texture.png");
    public static final int MAX_BEES_BEE_BOX = 10;

    public static final HoneyBottleData defaultHoney = new DefaultHoneyBottleData();
}
