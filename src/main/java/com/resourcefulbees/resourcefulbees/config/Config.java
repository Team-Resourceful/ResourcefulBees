package com.resourcefulbees.resourcefulbees.config;

import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {


    public static ForgeConfigSpec.BooleanValue GENERATE_DEFAULTS;
    public static ForgeConfigSpec.BooleanValue GENERATE_BIOME_DICTIONARIES;
    public static ForgeConfigSpec.BooleanValue USE_FORGE_DICTIONARIES;
    public static ForgeConfigSpec.BooleanValue ENABLE_EASTER_EGG_BEES;

    public static ForgeConfigSpec.IntValue BEE_FLOWERFOREST_MULTIPLIER;

    public static ForgeConfigSpec.BooleanValue GENERATE_BEE_NESTS;

    public static ForgeConfigSpec.BooleanValue CENTRIFUGE_RECIPES;
    public static ForgeConfigSpec.BooleanValue HONEYCOMB_BLOCK_RECIPES;

    public static ForgeConfigSpec.IntValue GLOBAL_CENTRIFUGE_RECIPE_TIME;
    public static ForgeConfigSpec.IntValue MULTIBLOCK_RECIPE_TIME_REDUCTION;

    public static ForgeConfigSpec.IntValue HIVE_MAX_BEES;
    public static ForgeConfigSpec.IntValue HIVE_MAX_COMBS;
    public static ForgeConfigSpec.BooleanValue ALLOW_SHEARS;

    public static ForgeConfigSpec.IntValue OVERWORLD_NEST_GENERATION_CHANCE;
    public static ForgeConfigSpec.IntValue NETHER_NEST_GENERATION_CHANCE;
    public static ForgeConfigSpec.IntValue END_NEST_GENERATION_CHANCE;

    public static ForgeConfigSpec.EnumValue<ApiaryOutput> T1_APIARY_OUTPUT;
    public static ForgeConfigSpec.EnumValue<ApiaryOutput> T2_APIARY_OUTPUT;
    public static ForgeConfigSpec.EnumValue<ApiaryOutput> T3_APIARY_OUTPUT;
    public static ForgeConfigSpec.EnumValue<ApiaryOutput> T4_APIARY_OUTPUT;

    public static ForgeConfigSpec.IntValue T1_APIARY_QUANTITY;
    public static ForgeConfigSpec.IntValue T2_APIARY_QUANTITY;
    public static ForgeConfigSpec.IntValue T3_APIARY_QUANTITY;
    public static ForgeConfigSpec.IntValue T4_APIARY_QUANTITY;

    public static ForgeConfigSpec.IntValue MAX_CENTRIFUGE_RF;
    public static ForgeConfigSpec.IntValue RF_TICK_CENTRIFUGE;
    public static ForgeConfigSpec.DoubleValue PLAYER_EXHAUSTION;
    public static ForgeConfigSpec.BooleanValue MULTIBLOCK_RECIPES_ONLY;

    public static ForgeConfigSpec.IntValue HONEY_FILL_AMOUNT;
    public static ForgeConfigSpec.IntValue HONEY_DRAIN_AMOUNT;
    public static ForgeConfigSpec.IntValue ENERGY_FILL_AMOUNT;
    public static ForgeConfigSpec.IntValue ENERGY_TRANSFER_AMOUNT;
    public static ForgeConfigSpec.IntValue MAX_ENERGY_CAPACITY;
    public static ForgeConfigSpec.IntValue MAX_TANK_STORAGE;

    public static ForgeConfigSpec.IntValue APIARY_MAX_BEES;
    public static ForgeConfigSpec.IntValue APIARY_MAX_BREED_TIME;

    public static ForgeConfigSpec.IntValue SMOKER_DURABILITY;

    public static ForgeConfigSpec.DoubleValue BEE_SIZE_MODIFIER;
    public static ForgeConfigSpec.DoubleValue CHILD_SIZE_MODIFIER;

    public static ForgeConfigSpec.BooleanValue BEE_DIES_FROM_STING;
    public static ForgeConfigSpec.BooleanValue BEES_INFLICT_POISON;

    public static ForgeConfigSpec.IntValue HONEYCOMB_HUNGER;
    public static ForgeConfigSpec.DoubleValue HONEYCOMB_SATURATION;

    public static ForgeConfigSpec.BooleanValue BEES_DIE_IN_VOID;

    public static ForgeConfigSpec.BooleanValue HONEY_GENERATE_FLUIDS;
    public static ForgeConfigSpec.BooleanValue HONEY_GENERATE_BLOCKS;
    public static ForgeConfigSpec.BooleanValue HONEY_BLOCK_RECIPIES;

    public static ForgeConfigSpec.BooleanValue BEECON_DO_MULTIPLIER;
    public static ForgeConfigSpec.IntValue BEECON_RANGE_PER_EFFECT;
    public static ForgeConfigSpec.DoubleValue BEECON_CALMING_VALUE;
    public static ForgeConfigSpec.DoubleValue BEECON_WATER_BREATHING_VALUE;
    public static ForgeConfigSpec.DoubleValue BEECON_FIRE_RESISTANCE_VALUE;
    public static ForgeConfigSpec.DoubleValue BEECON_REGENERATION_VALUE;
    public static ForgeConfigSpec.IntValue BEECON_BASE_DRAIN;


    //CLIENT

    public static ForgeConfigSpec.BooleanValue GENERATE_ENGLISH_LANG;
    public static ForgeConfigSpec.BooleanValue SHOW_DEBUG_INFO;


    public static class CommonConfig {

        public static ForgeConfigSpec COMMON_CONFIG;


        static {
            ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

            COMMON_BUILDER.push("General Options");
            GENERATE_DEFAULTS = COMMON_BUILDER.comment("\nSet this to false when you want to overwrite the default bee files. [true/false]\nThis should be run at least once for initial generation.")
                    .define("generateDefaults", true);
            GENERATE_BIOME_DICTIONARIES = COMMON_BUILDER.comment("\nSet this to false when you want to overwrite the default provided Biome Dictionary files. [true/false]\nThis should be run at least once for initial generation.")
                    .define("generateBiomeDictionaries", false);
            USE_FORGE_DICTIONARIES = COMMON_BUILDER.comment("\nSet this to false when you want to use our custom Biome Dictionary system. [true/false]\nAt a later time we will look into either combining with the Forge system or making the Forge system data pack capable.")
                    .define("useForgeBiomeDictionaries", true);
            ENABLE_EASTER_EGG_BEES = COMMON_BUILDER.comment("\nSet to true if you want easter egg bees to generate (WIP) [true/false]", "The only easter egg bee currently available is the Oreo bee")
                    .define("enableEasterEggBees", true);
            SMOKER_DURABILITY = COMMON_BUILDER.comment("\nSets the max durability for the smoker")
                    .defineInRange("smokerDurability", 1000, 100, 5000);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Recipe Options");
            CENTRIFUGE_RECIPES = COMMON_BUILDER.comment("\nSet to false if you don't want the centrifuge recipes to be auto generated [true/false]")
                    .define("centrifugeRecipes", true);
            HONEYCOMB_BLOCK_RECIPES = COMMON_BUILDER.comment("\nSet to false if you don't want the honeycomb block recipes to be auto generated [true/false]")
                    .define("honeycombBlockRecipes", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Centrifuge Options");
            GLOBAL_CENTRIFUGE_RECIPE_TIME = COMMON_BUILDER.comment("\nGlobal recipe time for generated centrifuge recipes", "This does not affect recipes that are not auto generated by us.", "Time is in ticks.")
                    .defineInRange("globalCentrifugeRecipeTime", 200, 100, 2400);
            MULTIBLOCK_RECIPE_TIME_REDUCTION = COMMON_BUILDER.comment("\nTick reduction applied to centrifuge recipes", "This does not affect recipes that are not auto generated by us.", "NOTE: Lowest recipe time allowed is 5 ticks regardless of values provided.")
                    .defineInRange("multiblockRecipeTimeReduction", 150, 10, 1200);
            MAX_CENTRIFUGE_RF = COMMON_BUILDER.comment("\nCentrifuge Max energy storage.\nThe Centrifuge Multiblocks max energy storage is 5x this amount")
                    .defineInRange("maxCentrifugeRf", 10000, 1000, 1000000);
            RF_TICK_CENTRIFUGE = COMMON_BUILDER.comment("\nRF/t consumed by the centrifuge when processing recipes. Mutliblock Centrifuge cuts this value in half.")
                    .defineInRange("centrifugeRfPerTick", 30, 2, 1000);
            PLAYER_EXHAUSTION = COMMON_BUILDER.comment("\nAmount of hunger the player uses per click on mechanical centrifuge.")
                    .defineInRange("mechanicalCentrifugePlayerExhaustion", 0.1, 0.0, 1);
            MULTIBLOCK_RECIPES_ONLY = COMMON_BUILDER.comment("\n Makes it so multiblock centrifuge can only do multiblock recipes. [true/false]")
                    .define("multiblockRecipesOnly", false);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Honey Generator Options");
            HONEY_FILL_AMOUNT = COMMON_BUILDER.comment("\nAmount of honey generated in mb/t. 1 bottle = 250mb honey")
                    .defineInRange("honeyFillAmount", 10, 1, 50);
            HONEY_DRAIN_AMOUNT = COMMON_BUILDER.comment("\nAmount of honey consumed in mb/t.")
                    .defineInRange("honeyDrainAmount", 5, 1, 50);
            ENERGY_FILL_AMOUNT = COMMON_BUILDER.comment("\nAmount of rf/t generated.")
                    .defineInRange("energyFillAmount", 125, 0, 500);
            ENERGY_TRANSFER_AMOUNT = COMMON_BUILDER.comment("\nAmount of energy transferred out of the generator in rf/t.")
                    .defineInRange("energyTransferAmount", 100, 50, 500);
            MAX_ENERGY_CAPACITY = COMMON_BUILDER.comment("\nMaximum internal energy buffer.")
                    .defineInRange("maxEnergyCapacity", 100000, 10000, 1000000);
            MAX_TANK_STORAGE = COMMON_BUILDER.comment("\nMaximum internal honey capacity.")
                    .defineInRange("maxTankCapacity", 10000, 1000, 100000);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Ender Beecon Options");
            BEECON_DO_MULTIPLIER = COMMON_BUILDER.comment("\nIf true, the below values will be mulitplied with the base value, if false they will be added instead.")
                    .define("beeconDoMultiplier", true);
            BEECON_CALMING_VALUE = COMMON_BUILDER.comment("\nMultiplier for the drain rate for the Ender Beecon when the Calming effect is active.")
                    .defineInRange("beeconCalmingValue", 2d, 1d, 128d);
            BEECON_WATER_BREATHING_VALUE = COMMON_BUILDER.comment("\nMultiplier for the drain rate for the Ender Beecon when the Water Breathing effect is active.")
                    .defineInRange("beeconWaterBreathingValue", 1.5d, 1d, 128d);
            BEECON_FIRE_RESISTANCE_VALUE = COMMON_BUILDER.comment("\nMultiplier for the drain rate for the Ender Beecon when the Fire Resistance effect is active.")
                    .defineInRange("beeconFireResistanceValue", 2d, 1d, 128d);
            BEECON_REGENERATION_VALUE = COMMON_BUILDER.comment("\nMultiplier for the drain rate for the Ender Beecon when the Regeneration effect is active.")
                    .defineInRange("beeconRegenerationValue", 2.5d, 1d, 128d);
            BEECON_RANGE_PER_EFFECT = COMMON_BUILDER.comment("\nRange in blocks added for each effect that is currently active.")
                    .defineInRange("beeconRangePerEffect", 10, 1, 25);
            BEECON_BASE_DRAIN = COMMON_BUILDER.comment("\nThe base drain rate for the Ender Beecon when an effect is active.")
                    .defineInRange("beeconBaseDrain", 1, 1, 128);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Beehive Options");
            HIVE_MAX_BEES = COMMON_BUILDER.comment("\nMaximum number of bees in the base tier hive. \n(THIS * TIER_MODIFIER = MAX_BEES) for a range of 4 -> 16")
                    .defineInRange("hiveMaxBees", 4, 1, 4);
            HIVE_MAX_COMBS = COMMON_BUILDER.comment("\nBase honeycomb harvest amount \n(THIS * TIER_MODIFIER = MAX_COMBS) for a range of 5 -> 64")
                    .defineInRange("hiveMaxCombs", 5, 5, 16);
            ALLOW_SHEARS = COMMON_BUILDER.comment("\nSet to false if you want the player to only be able to get honeycombs from the beehive using the scraper [true/false]")
                    .define("allowShears", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Apiary Options");
            T1_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 1 Apiary Output")
                    .defineEnum("tierOneApiaryOutput", ApiaryOutput.COMB, ApiaryOutput.COMB, ApiaryOutput.BLOCK);
            T1_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 1 Apiary Output Quantity")
                    .defineInRange("tierOneApiaryQuantity", 8, 1, Integer.MAX_VALUE);
            T2_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 2 Apiary Output")
                    .defineEnum("tierTwoApiaryOutput", ApiaryOutput.COMB, ApiaryOutput.COMB, ApiaryOutput.BLOCK);
            T2_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 2 Apiary Output Quantity")
                    .defineInRange("tierTwoApiaryQuantity", 16, 1, Integer.MAX_VALUE);
            T3_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 3 Apiary Output")
                    .defineEnum("tierThreeApiaryOutput", ApiaryOutput.BLOCK, ApiaryOutput.COMB, ApiaryOutput.BLOCK);
            T3_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 3 Apiary Output Quantity")
                    .defineInRange("tierThreeApiaryQuantity", 4, 1, Integer.MAX_VALUE);
            T4_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 4 Apiary Output")
                    .defineEnum("tierFourApiaryOutput", ApiaryOutput.BLOCK, ApiaryOutput.COMB, ApiaryOutput.BLOCK);
            T4_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 4 Apiary Output Quantity")
                    .defineInRange("tierFourApiaryQuantity", 8, 1, Integer.MAX_VALUE);
            APIARY_MAX_BEES = COMMON_BUILDER.comment("\nMaximum number of UNIQUE bees allowed in the Apiary.")
                    .defineInRange("apiaryMaxBees", 9, 1, 16);
            APIARY_MAX_BREED_TIME = COMMON_BUILDER.comment("\nMaximum breed time before upgrades are applied.")
                    .defineInRange("apiaryMaxBreedTime", 2400, 1200, 4800);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Spawning Options");
            GENERATE_BEE_NESTS = COMMON_BUILDER.comment("\nShould bee nests generate in world? \nNote: They will only generate in biomes where bees can spawn")
                    .define("generateBeeNests", true);
            BEE_FLOWERFOREST_MULTIPLIER = COMMON_BUILDER.comment("The value added to weight for bees in a flower forests")
                    .defineInRange("beesMoreCommonInFlowerForests", 4, 0, 9);
            OVERWORLD_NEST_GENERATION_CHANCE = COMMON_BUILDER.comment("\nChance for nest to spawn when generating chunks in overworld category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                    .defineInRange("overworld_nest_generation_chance", 48, 4, 100);
            NETHER_NEST_GENERATION_CHANCE = COMMON_BUILDER.comment("\nChance for nest to spawn when generating chunks in nether category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                    .defineInRange("nether_nest_generation_chance", 8, 4, 100);
            END_NEST_GENERATION_CHANCE = COMMON_BUILDER.comment("\nChance for nest to spawn when generating chunks in end category biomes. [1/x]\nA higher value means the nest is less likely to spawn.")
                    .defineInRange("end_nest_generation_chance", 32, 4, 100);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Bee Options");
            BEE_SIZE_MODIFIER = COMMON_BUILDER.comment("\nThis value scales the bee size for all Resource Bees. \nNote: Setting the value in bee JSON overrides this value.")
                    .defineInRange("global_bee_size_modifier", 1.0, 0.5, 2.0);
            CHILD_SIZE_MODIFIER = COMMON_BUILDER.comment("\nThis value scales the child size for all Resource Bees.")
                    .defineInRange("global_child_size_modifier", 1.0, 1.0, 2.0);
            BEE_DIES_FROM_STING = COMMON_BUILDER.comment("\nShould bees die from stinging?\nNote: Bees will continue to attack until they are no longer angry!")
                    .define("beeDiesFromSting", true); //TODO 1.17 change to "beesDieFromSting"
            BEES_INFLICT_POISON = COMMON_BUILDER.comment("\nShould bees inflict poison damage?\nNote: Poison is only inflicted if a bee has not been given a trait with a special damage output.\nSet to false if you want to configure bees individually.")
                    .define("beesInflictPoison", true);
            BEES_DIE_IN_VOID = COMMON_BUILDER.comment("\nShould bees die when their Y-level is below 0?\nNote: If false, bees will get stuck just below y-0 and not move. **May not be useful with new AI**")
                    .define("beeDiesInVoid", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Honeycomb Options");
            HONEYCOMB_HUNGER = COMMON_BUILDER.comment("\nThe amount of hunger restored when eating a honeycomb.")
                    .defineInRange("honeycombHunger", 1, 0, 8);
            HONEYCOMB_SATURATION = COMMON_BUILDER.comment("\nThe amount of saturation restored when eating a honeycomb.")
                    .defineInRange("honeycombSaturation", 0.6, 0, 8.0);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Honey Options");
            HONEY_GENERATE_FLUIDS = COMMON_BUILDER.comment("\nSet to false if you don't want the custom honey fluids to be generated [true/false]")
                    .define("generateHoneyFluids", true);
            HONEY_GENERATE_BLOCKS = COMMON_BUILDER.comment("\nSet to false if you don't want the custom honey blocks to be generated [true/false]")
                    .define("generateHoneyBlocks", true);
            HONEY_BLOCK_RECIPIES = COMMON_BUILDER.comment("\nShould honey block recipies be generated? [true/false]")
                    .define("honeyBlockRecipies", true);

            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec CLIENT_CONFIG;

        static {
            ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

            CLIENT_BUILDER.push("General Options");
            GENERATE_ENGLISH_LANG = CLIENT_BUILDER.comment("\nWhen set to true an en_us.json file will be generated for the bees. [true/false] \n This file will be overwritten every time the mod loads. \n The generated names are based on the bee jsons. \nThis is best used by pack devs as a one-time run.")
                    .define("generateEnglishLang", false);
            SHOW_DEBUG_INFO = CLIENT_BUILDER.comment("\nWhen set to true will display some debug info in console. [true/false]")
                    .define("showDebugInfo", false);
            CLIENT_BUILDER.pop();

            CLIENT_CONFIG = CLIENT_BUILDER.build();
        }
    }
}