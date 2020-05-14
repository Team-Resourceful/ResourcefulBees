package com.dungeonderps.resourcefulbees.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ResourcefulBeesConfig {

    public static final String MOD_OPTIONS = "Mod Options";

    public static ForgeConfigSpec.BooleanValue GENERATE_DEFAULTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_EASTER_EGG_BEES;
    public static ForgeConfigSpec.BooleanValue DEBUG_MODE;  //TODO possibly remove DEBUG Config option.
    public static ForgeConfigSpec.DoubleValue HIVE_OUTPUT_MODIFIER;
    public static ForgeConfigSpec.IntValue HIVE_MAX_BEES;

    public static class CommonConfig {

        public static ForgeConfigSpec COMMON_CONFIG;

        static {
            ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

            COMMON_BUILDER.comment("Mod options").push(MOD_OPTIONS);
            COMMON_BUILDER.pop();

            GENERATE_DEFAULTS = COMMON_BUILDER.comment("Set to true if you want our default bees to populate the bees folder [true/false]")
                    .define("generateDefaults",true);
            ENABLE_EASTER_EGG_BEES = COMMON_BUILDER.comment("Set to true if you want easter egg bees to generate [true/false]")
                    .define("enableEasterEggBees", true);
            DEBUG_MODE = COMMON_BUILDER.comment("Extra logger info [true/false]")
                    .define("debugMode", false);
            HIVE_OUTPUT_MODIFIER = COMMON_BUILDER.comment("Output modifier for the hives when ready to be harvested[range 0.0 - 8.0]")
                    .defineInRange("hiveOutputModifier", 1.0,0.0,8.0);
            HIVE_MAX_BEES = COMMON_BUILDER.comment("Maximum amount of bees in the hive at any given time[range 0 - 16")
                    .defineInRange("hiveMaxBees", 4, 0, 16);

            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }
}
