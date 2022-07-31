package com.teamresourceful.resourcefulbees.common.lib.constants;

public final class NBTConstants {

    private NBTConstants() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final String NBT_BEEPEDIA_DATA = "BeepediaData";
    public static final String NBT_BEES = "Bees";
    public static final String NBT_DISPLAYNAMES = "DisplayNames";
    public static final String NBT_BEE_NAME = "DisplayName";
    public static final String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String NBT_ENERGY = "energy";
    public static final String NBT_FEED_COUNT = "FeedCount";
    public static final String NBT_FILTER_INVENTORY = "filterInv";
    public static final String NBT_FILTER_RECIPE = "filterRecipe";
    public static final String NBT_FLUID_OUTPUTS = "fluidOutputs";
    public static final String NBT_ID = "id";
    public static final String NBT_INVENTORY = "inv";
    public static final String NBT_ITEM_OUTPUTS = "itemOutputs";
    public static final String NBT_LOCATIONS = "locations";
    public static final String NBT_LOCKED = "Locked";
    public static final String NBT_MUTATION_COUNT = "mutationCount";
    public static final String NBT_PROCESS_ENERGY = "processEnergy";
    public static final String NBT_PROCESS_RECIPE = "processRecipe";
    public static final String NBT_PROCESS_STAGE = "processStage";
    public static final String NBT_PROCESS_TIME = "processTime";
    public static final String NBT_TANK = "Tank";
    public static final String ROTATIONS = "Rotations";
    public static final String SYNC_DATA = "SyncData";

    public static class Beecon {
        public static final String RANGE = "range";
        public static final String ACTIVE_EFFECTS = "active_effects";
    }

    public static class Breeder {
        public static final String TIME_REDUCTION = "TimeReduction";
    }

    public static class BeeJar {
        public static final String COLOR = "Color";
        public static final String ENTITY = "Entity";
        public static final String DISPLAY_NAME = "DisplayName";
    }

    public static class HoneyDipper {
        public static final String Entity = "Entity";
    }

    public static class BeeHive {
        public static final String SMOKED = "Smoked";
        public static final String HONEYCOMBS = "Honeycombs";
    }
}
