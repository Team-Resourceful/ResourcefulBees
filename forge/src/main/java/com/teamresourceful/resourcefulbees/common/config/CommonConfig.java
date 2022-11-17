package com.teamresourceful.resourcefulbees.common.config;

import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public final class CommonConfig {
    //TODO Rewrite config names in 1.17 to be more informative also update comments if needed

    public static BooleanValue GENERATE_DEFAULTS;
    public static BooleanValue GENERATE_DEFAULT_RECIPES;
    public static BooleanValue ENABLE_DEV_BEES;

    public static BooleanValue GENERATE_BEE_NESTS;

    public static BooleanValue CENTRIFUGE_RECIPES;
    public static BooleanValue HONEYCOMB_BLOCK_RECIPES;

    public static IntValue GLOBAL_CENTRIFUGE_RECIPE_TIME;
    public static IntValue MULTIBLOCK_RECIPE_TIME_REDUCTION;

    public static IntValue HIVE_MAX_BEES;
    public static IntValue HIVE_MAX_COMBS;
    public static BooleanValue ALLOW_SHEARS;

    public static IntValue OVERWORLD_NEST_GENERATION_CHANCE;
    public static IntValue NETHER_NEST_GENERATION_CHANCE;
    public static IntValue END_NEST_GENERATION_CHANCE;

    public static EnumValue<ApiaryOutputType> T1_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputType> T2_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputType> T3_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputType> T4_APIARY_OUTPUT;

    public static IntValue T1_APIARY_QUANTITY;
    public static IntValue T2_APIARY_QUANTITY;
    public static IntValue T3_APIARY_QUANTITY;
    public static IntValue T4_APIARY_QUANTITY;

    public static IntValue MAX_CENTRIFUGE_RF;
    public static IntValue MAX_CENTRIFUGE_RECEIVE_RATE;
    public static IntValue RF_TICK_CENTRIFUGE;
    public static DoubleValue PLAYER_EXHAUSTION;
    public static BooleanValue MULTIBLOCK_RECIPES_ONLY;

    public static IntValue HONEY_FILL_AMOUNT;
    public static IntValue HONEY_DRAIN_AMOUNT;
    public static IntValue HONEY_PROCESS_TIME;
    public static IntValue CONGEALER_TIME_MODIFIER;
    public static IntValue ENERGY_FILL_AMOUNT;
    public static IntValue ENERGY_TRANSFER_AMOUNT;
    public static IntValue MAX_ENERGY_CAPACITY;
    public static IntValue MAX_TANK_STORAGE;

    public static IntValue APIARY_MAX_BEES;
    //public static IntValue APIARY_MAX_BREED_TIME;

    public static IntValue SMOKER_DURABILITY;

    public static DoubleValue BEE_SIZE_MODIFIER;
    public static DoubleValue CHILD_SIZE_MODIFIER;

    public static BooleanValue BEES_DIE_FROM_STING;
    public static BooleanValue BEES_INFLICT_POISON;

    public static BooleanValue EDIBLE_HONEYCOMBS;
    public static IntValue HONEYCOMB_HUNGER;
    public static DoubleValue HONEYCOMB_SATURATION;

    public static BooleanValue BEES_DIE_IN_VOID;

    public static BooleanValue HONEY_GENERATE_FLUIDS;
    public static BooleanValue HONEY_GENERATE_BLOCKS;
    public static BooleanValue HONEY_BLOCK_RECIPES;

    public static DoubleValue BEECON_CALMING_VALUE;
    public static DoubleValue BEECON_WATER_BREATHING_VALUE;
    public static DoubleValue BEECON_FIRE_RESISTANCE_VALUE;
    public static DoubleValue BEECON_REGENERATION_VALUE;
    public static DoubleValue BEECON_RANGE_MULTIPLIER;
    public static IntValue BEECON_BASE_DRAIN;
    public static IntValue BEECON_PULL_AMOUNT;

    public static BooleanValue BYPASS_PERFORMANT_CHECK;
    public static BooleanValue BEEPEDIA_HIDE_LOCKED;
    public static IntValue DEFAULT_AURA_RANGE;

    public static BooleanValue MANUAL_MODE;

    public static BooleanValue SHOW_DEBUG_INFO;

    private CommonConfig() {
        throw new UtilityClassError();
    }

    public static final ForgeConfigSpec COMMON_CONFIG;

    static {
        Builder commonBuilder = new Builder();

        commonBuilder.push("General Options");
        GENERATE_DEFAULTS = commonBuilder.comment("\nSet this to false when you want to overwrite the default bee files. [true/false]\nThis should be run at least once for initial generation.")
                .define("generateDefaults", true);
        GENERATE_DEFAULT_RECIPES = commonBuilder.comment("\nSet this to false when you want to overwrite the default bees recipes. [true/false]")
                .define("generateDefaultsRecipes", true);
        ENABLE_DEV_BEES = commonBuilder.comment("\nSet to true if you want dev bees to generate. [true/false]")
                .define("enableDevBees", true);
        SMOKER_DURABILITY = commonBuilder.comment("\nSets the max durability for the smoker")
                .defineInRange("smokerDurability", 1000, 100, 5000);
        BEEPEDIA_HIDE_LOCKED = commonBuilder.comment("\nSet to true to hide certain data in the Beepedia until the player has unlocked the bee. [true/false]")
                .define("beepediaHideLocked", false);
        HONEY_PROCESS_TIME = commonBuilder.comment("\nAmount of time in ticks required to finish processing a honey bottle.")
                .defineInRange("honeyProcessTime", 5, 0, 2400);
        CONGEALER_TIME_MODIFIER = commonBuilder.comment("\nMultiplier to the amount of ticks needed to process honey into honey blocks in the congealer.\nThis value is multiplying the honeyProcessTime.")
                .defineInRange("congealerTimeMultiplier", 4, 1, 16);
        commonBuilder.pop();

        commonBuilder.push("Recipe Options");
        CENTRIFUGE_RECIPES = commonBuilder.comment("\nSet to false if you don't want the centrifuge recipes to be auto generated [true/false]")
                .define("centrifugeRecipes", true);
        HONEYCOMB_BLOCK_RECIPES = commonBuilder.comment("\nSet to false if you don't want the honeycomb block recipes to be auto generated [true/false]")
                .define("honeycombBlockRecipes", true);
        commonBuilder.pop();

        commonBuilder.push("Centrifuge Options");
        GLOBAL_CENTRIFUGE_RECIPE_TIME = commonBuilder.comment("\nGlobal recipe time for generated centrifuge recipes", "This does not affect recipes that are not auto generated by us.", "Time is in ticks.")
                .defineInRange("globalCentrifugeRecipeTime", 200, 100, 2400);
        MULTIBLOCK_RECIPE_TIME_REDUCTION = commonBuilder.comment("\nTick reduction applied to centrifuge recipes", "This does not affect recipes that are not auto generated by us.", "NOTE: Lowest recipe time allowed is 5 ticks regardless of values provided.")
                .defineInRange("multiblockRecipeTimeReduction", 150, 10, 1200);
        MAX_CENTRIFUGE_RF = commonBuilder.comment("\nCentrifuge Max energy storage.\nThe Centrifuge Multiblocks max energy storage is 5x this amount")
                .defineInRange("maxCentrifugeRf", 10000, 1000, 1000000);
        RF_TICK_CENTRIFUGE = commonBuilder.comment("\nRF/t consumed by the centrifuge when processing recipes. Mutliblock Centrifuge cuts this value in half.")
                .defineInRange("centrifugeRfPerTick", 30, 2, 1000);
        PLAYER_EXHAUSTION = commonBuilder.comment("\nAmount of hunger the player uses per click on mechanical centrifuge.")
                .defineInRange("mechanicalCentrifugePlayerExhaustion", 0.1, 0.0, 1);
        MULTIBLOCK_RECIPES_ONLY = commonBuilder.comment("\nMakes it so multiblock centrifuge can only do multiblock recipes. [true/false]")
                .define("multiblockRecipesOnly", false);
        MAX_CENTRIFUGE_RECEIVE_RATE = commonBuilder.comment("\nSets the max RF/t that the centrifuge can receive. This should ideally be set higher than centrifugeRfPerTick.")
                .defineInRange("maxCentrifugeReceiveRate", 1, 100, 10000);
        commonBuilder.pop();

        commonBuilder.push("Honey Generator Options");
        HONEY_FILL_AMOUNT = commonBuilder.comment("\nAmount of honey generated in mb/t. 1 bottle = 250mb honey")
                .defineInRange("honeyFillAmount", 10, 1, 50);
        HONEY_DRAIN_AMOUNT = commonBuilder.comment("\nAmount of honey consumed in mb/t.")
                .defineInRange("honeyDrainAmount", 5, 1, 50);
        ENERGY_FILL_AMOUNT = commonBuilder.comment("\nAmount of rf/t generated.")
                .defineInRange("energyFillAmount", 125, 0, 500);
        ENERGY_TRANSFER_AMOUNT = commonBuilder.comment("\nAmount of energy transferred out of the generator in rf/t.")
                .defineInRange("energyTransferAmount", 100, 50, 500);
        MAX_ENERGY_CAPACITY = commonBuilder.comment("\nMaximum internal energy buffer.")
                .defineInRange("maxEnergyCapacity", 100000, 10000, 1000000);
        MAX_TANK_STORAGE = commonBuilder.comment("\nMaximum internal honey capacity.")
                .defineInRange("maxTankCapacity", 10000, 1000, 100000);
        commonBuilder.pop();

        commonBuilder.push("Ender Beecon Options");
        BEECON_CALMING_VALUE = commonBuilder.comment("\nMultiplier for the drain rate for the Ender Beecon when the Calming effect is active.")
                .defineInRange("beeconCalmingValue", 2d, 1d, 128d);
        BEECON_WATER_BREATHING_VALUE = commonBuilder.comment("\nMultiplier for the drain rate for the Ender Beecon when the Water Breathing effect is active.")
                .defineInRange("beeconWaterBreathingValue", 1.5d, 1d, 128d);
        BEECON_FIRE_RESISTANCE_VALUE = commonBuilder.comment("\nMultiplier for the drain rate for the Ender Beecon when the Fire Resistance effect is active.")
                .defineInRange("beeconFireResistanceValue", 2d, 1d, 128d);
        BEECON_REGENERATION_VALUE = commonBuilder.comment("\nMultiplier for the drain rate for the Ender Beecon when the Regeneration effect is active.")
                .defineInRange("beeconRegenerationValue", 2.5d, 1d, 128d);
        BEECON_RANGE_MULTIPLIER = commonBuilder.comment("\nMultiplier for each level of range applied to the Ender Beecon's drain.")
                .defineInRange("beeconRangeMultiplier", 0.33, 0, 2);
        BEECON_BASE_DRAIN = commonBuilder.comment("\nThe base drain rate for the Ender Beecon when an effect is active.")
                .defineInRange("beeconBaseDrain", 1, 1, 128);
        BEECON_PULL_AMOUNT = commonBuilder.comment("\nThe amount of fluid per tick the Ender Beecon can pull from below blocks.")
                .defineInRange("beeconPullAmount", 250, 1, 16000);
        commonBuilder.pop();

        commonBuilder.push("Beehive Options");
        HIVE_MAX_BEES = commonBuilder.comment("\nMaximum number of bees in the base tier hive. \n(THIS * TIER_MODIFIER = MAX_BEES) for a range of 4 -> 16")
                .defineInRange("hiveMaxBees", 4, 1, 4);
        HIVE_MAX_COMBS = commonBuilder.comment("\nBase honeycomb harvest amount \n(THIS * TIER_MODIFIER = MAX_COMBS) for a range of 5 -> 64")
                .defineInRange("hiveMaxCombs", 5, 5, 16);
        ALLOW_SHEARS = commonBuilder.comment("\nSet to false if you want the player to only be able to get honeycombs from the beehive using the scraper [true/false]")
                .define("allowShears", true);
        commonBuilder.pop();

        commonBuilder.push("Apiary Options");
        T1_APIARY_OUTPUT = commonBuilder.comment("\nTier 1 Apiary Output")
                .defineEnum("tierOneApiaryOutput", ApiaryOutputType.COMB, ApiaryOutputType.COMB, ApiaryOutputType.BLOCK);
        T1_APIARY_QUANTITY = commonBuilder.comment("\nTier 1 Apiary Output Quantity")
                .defineInRange("tierOneApiaryQuantity", 8, 1, Integer.MAX_VALUE);
        T2_APIARY_OUTPUT = commonBuilder.comment("\nTier 2 Apiary Output")
                .defineEnum("tierTwoApiaryOutput", ApiaryOutputType.COMB, ApiaryOutputType.COMB, ApiaryOutputType.BLOCK);
        T2_APIARY_QUANTITY = commonBuilder.comment("\nTier 2 Apiary Output Quantity")
                .defineInRange("tierTwoApiaryQuantity", 16, 1, Integer.MAX_VALUE);
        T3_APIARY_OUTPUT = commonBuilder.comment("\nTier 3 Apiary Output")
                .defineEnum("tierThreeApiaryOutput", ApiaryOutputType.BLOCK, ApiaryOutputType.COMB, ApiaryOutputType.BLOCK);
        T3_APIARY_QUANTITY = commonBuilder.comment("\nTier 3 Apiary Output Quantity")
                .defineInRange("tierThreeApiaryQuantity", 4, 1, Integer.MAX_VALUE);
        T4_APIARY_OUTPUT = commonBuilder.comment("\nTier 4 Apiary Output")
                .defineEnum("tierFourApiaryOutput", ApiaryOutputType.BLOCK, ApiaryOutputType.COMB, ApiaryOutputType.BLOCK);
        T4_APIARY_QUANTITY = commonBuilder.comment("\nTier 4 Apiary Output Quantity")
                .defineInRange("tierFourApiaryQuantity", 8, 1, Integer.MAX_VALUE);
        APIARY_MAX_BEES = commonBuilder.comment("\nMaximum number of UNIQUE bees allowed in the Apiary.")
                .defineInRange("apiaryMaxBees", 16, 1, 24);
        commonBuilder.pop();

        commonBuilder.push("Spawning Options");
        GENERATE_BEE_NESTS = commonBuilder.comment("\nShould bee nests generate in world? \nNote: They will only generate in biomes where bees can spawn")
                .define("generateBeeNests", true);
        OVERWORLD_NEST_GENERATION_CHANCE = commonBuilder.comment("\nChance for nest to spawn when generating chunks in overworld category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                .defineInRange("overworld_nest_generation_chance", 48, 4, 100);
        NETHER_NEST_GENERATION_CHANCE = commonBuilder.comment("\nChance for nest to spawn when generating chunks in nether category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                .defineInRange("nether_nest_generation_chance", 8, 4, 100);
        END_NEST_GENERATION_CHANCE = commonBuilder.comment("\nChance for nest to spawn when generating chunks in end category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                .defineInRange("end_nest_generation_chance", 32, 4, 100);
        commonBuilder.pop();

        commonBuilder.push("Bee Options");
        BEE_SIZE_MODIFIER = commonBuilder.comment("\nThis value scales the bee size for all Resource Bees. \nNote: Setting the value in bee JSON overrides this value.")
                .defineInRange("global_bee_size_modifier", 1.0, 0.5, 2.0);
        CHILD_SIZE_MODIFIER = commonBuilder.comment("\nThis value scales the child size for all Resource Bees.")
                .defineInRange("global_child_size_modifier", 1.0, 1.0, 2.0);
        BEES_DIE_FROM_STING = commonBuilder.comment("\nShould bees die from stinging?\nNote: Bees will continue to attack until they are no longer angry!")
                .define("beesDieFromSting", true);
        BEES_INFLICT_POISON = commonBuilder.comment("\nShould bees inflict poison damage?\nNote: Poison is only inflicted if a bee has not been given a trait with a special damage output.\nSet to false if you want to configure bees individually.")
                .define("beesInflictPoison", true);
        BEES_DIE_IN_VOID = commonBuilder.comment("\nShould bees die when their Y-level is below 0?\nNote: If false, bees will get stuck just below y-0 and not move. **May not be useful with new AI**")
                .define("beesDieInVoid", true); //TODO change comment above to reflect 1.17 y level changes
        MANUAL_MODE = commonBuilder.comment("\nThis is an experimental setting. Using this setting means bees will need to be told by the player which flower and hive to use.",
                "Bees will not scan surroundings for flowers or hives and will instead go to their designated spot until changed.",
                "WARNING: For now, this will prevent bees from having their wander goal attached which effectively makes them dumb (seriously, they'll just hover in one spot), however it would also significantly improve performance until pathfinding can be optimized.")
                .define("use_experimental_manual_mode", false);
        DEFAULT_AURA_RANGE = commonBuilder.comment("\nThe default radius that all bees will use for their auras.")
                .defineInRange("beeAuraRange", 10, 3, 20);
        commonBuilder.pop();

        commonBuilder.push("Honeycomb Options");
        EDIBLE_HONEYCOMBS = commonBuilder.comment("\nWhether all honeycombs should be edible by default or not.").
                define("honeycombsEdible", true);
        HONEYCOMB_HUNGER = commonBuilder.comment("\nThe amount of hunger restored when eating a honeycomb.")
                .defineInRange("honeycombHunger", 1, 0, 8);
        HONEYCOMB_SATURATION = commonBuilder.comment("\nThe amount of saturation restored when eating a honeycomb.")
                .defineInRange("honeycombSaturation", 0.6, 0, 8.0);
        commonBuilder.pop();

        commonBuilder.push("Honey Options");
        HONEY_GENERATE_FLUIDS = commonBuilder.comment("\nSet to false if you don't want the custom honey fluids to be generated [true/false]")
                .define("generateHoneyFluids", true);
        HONEY_GENERATE_BLOCKS = commonBuilder.comment("\nSet to false if you don't want the custom honey blocks to be generated [true/false]")
                .define("generateHoneyBlocks", true);
        HONEY_BLOCK_RECIPES = commonBuilder.comment("\nShould honey block recipes be generated? [true/false]")
                .define("honeyBlockRecipes", true);
        commonBuilder.pop();

        commonBuilder.push("Mod Options");
        BYPASS_PERFORMANT_CHECK = commonBuilder.comment("Set this to true if you wish to live life on the edge!", "Seriously though it is recommended this only be used for testing purposes!!!")
                .define("bypassPerformantCheck", false);
        SHOW_DEBUG_INFO = commonBuilder.comment("\nWhen set to true will display some debug info in console. [true/false]")
                .define("showDebugInfo", false);
        commonBuilder.pop();

        COMMON_CONFIG = commonBuilder.build();
    }
}