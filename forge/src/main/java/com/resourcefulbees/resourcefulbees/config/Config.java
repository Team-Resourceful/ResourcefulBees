package com.resourcefulbees.resourcefulbees.config;

import com.resourcefulbees.resourcefulbees.lib.ApiaryOutputs;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public class Config {



    //TODO Rewrite config names in 1.17 to be more informative also update comments if needed

    private Config() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static BooleanValue GENERATE_DEFAULTS;
    public static BooleanValue GENERATE_BIOME_DICTIONARIES;
    public static BooleanValue USE_FORGE_DICTIONARIES;
    public static BooleanValue ENABLE_EASTER_EGG_BEES;

    public static IntValue BEE_FLOWER_FOREST_MULTIPLIER;

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

    public static EnumValue<ApiaryOutputs> T1_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputs> T2_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputs> T3_APIARY_OUTPUT;
    public static EnumValue<ApiaryOutputs> T4_APIARY_OUTPUT;

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
    public static IntValue MAX_PIPE_FLOW;
    public static IntValue ENERGY_FILL_AMOUNT;
    public static IntValue ENERGY_TRANSFER_AMOUNT;
    public static IntValue MAX_ENERGY_CAPACITY;
    public static IntValue MAX_TANK_STORAGE;

    public static IntValue APIARY_MAX_BEES;
    public static IntValue APIARY_MAX_BREED_TIME;

    public static IntValue SMOKER_DURABILITY;

    public static DoubleValue BEE_SIZE_MODIFIER;
    public static DoubleValue CHILD_SIZE_MODIFIER;

    public static BooleanValue BEE_DIES_FROM_STING;
    public static BooleanValue BEES_INFLICT_POISON;

    public static IntValue HONEYCOMB_HUNGER;
    public static DoubleValue HONEYCOMB_SATURATION;

    public static BooleanValue BEES_DIE_IN_VOID;

    public static BooleanValue HONEY_GENERATE_FLUIDS;
    public static BooleanValue HONEY_GENERATE_BLOCKS;
    public static BooleanValue HONEY_BLOCK_RECIPES;

    public static BooleanValue BEECON_DO_MULTIPLIER;
    public static IntValue BEECON_RANGE_PER_EFFECT;
    public static DoubleValue BEECON_CALMING_VALUE;
    public static DoubleValue BEECON_WATER_BREATHING_VALUE;
    public static DoubleValue BEECON_FIRE_RESISTANCE_VALUE;
    public static DoubleValue BEECON_REGENERATION_VALUE;
    public static IntValue BEECON_BASE_DRAIN;
    public static IntValue BEECON_PULL_AMOUNT;

    public static BooleanValue BYPASS_PERFORMANT_CHECK;
    public static BooleanValue BEEPEDIA_HIDE_LOCKED;

    public static BooleanValue MANUAL_MODE;

    //CLIENT

    public static BooleanValue GENERATE_ENGLISH_LANG;
    public static BooleanValue SHOW_DEBUG_INFO;

    public static class CommonConfig {

        private CommonConfig() {
            throw new IllegalStateException(ModConstants.UTILITY_CLASS);
        }

        public static final ForgeConfigSpec COMMON_CONFIG;


        static {
            Builder COMMON_BUILDER = new Builder();

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
            BEEPEDIA_HIDE_LOCKED = COMMON_BUILDER.comment("\nSet to true to hide certain data in the Beepedia until the player has unlocked the bee. [true/false]")
                    .define("beepediaHideLocked", false);
            HONEY_PROCESS_TIME = COMMON_BUILDER.comment("\nAmount of time in ticks required to finish processing a honey bottle.")
                    .defineInRange("honeyProcessTime", 5, 0, 2400);
            CONGEALER_TIME_MODIFIER = COMMON_BUILDER.comment("\nMultiplier to the amount of ticks needed to process honey into honey blocks in the congealer." +
                    "\nThis value is multiplying the honeyProcessTime.")
                    .defineInRange("congealerTimeMultiplier", 4, 1, 16);
            MAX_PIPE_FLOW = COMMON_BUILDER.comment("\nAmount of honey Honey Pipes can transfer per tick.")
                    .defineInRange("maxHoneyPipeFlow", 250, 10, 16000);
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
            MULTIBLOCK_RECIPES_ONLY = COMMON_BUILDER.comment("\nMakes it so multiblock centrifuge can only do multiblock recipes. [true/false]")
                    .define("multiblockRecipesOnly", false);
            MAX_CENTRIFUGE_RECEIVE_RATE = COMMON_BUILDER.comment("\nSets the max RF/t that the centrifuge can receive. This should ideally be set higher than centrifugeRfPerTick.")
                    .defineInRange("maxCentrifugeReceiveRate", 1, 100, 10000);
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
            BEECON_DO_MULTIPLIER = COMMON_BUILDER.comment("\nIf true, the below values will be multiplied with the base value, if false they will be added instead.")
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
            BEECON_PULL_AMOUNT = COMMON_BUILDER.comment("\nThe amount of fluid per tick the Ender Beecon can pull from below blocks.")
                    .defineInRange("beeconPullAmount", 250, 1, 16000);
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
                    .defineEnum("tierOneApiaryOutput", ApiaryOutputs.COMB, ApiaryOutputs.COMB, ApiaryOutputs.BLOCK);
            T1_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 1 Apiary Output Quantity")
                    .defineInRange("tierOneApiaryQuantity", 8, 1, Integer.MAX_VALUE);
            T2_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 2 Apiary Output")
                    .defineEnum("tierTwoApiaryOutput", ApiaryOutputs.COMB, ApiaryOutputs.COMB, ApiaryOutputs.BLOCK);
            T2_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 2 Apiary Output Quantity")
                    .defineInRange("tierTwoApiaryQuantity", 16, 1, Integer.MAX_VALUE);
            T3_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 3 Apiary Output")
                    .defineEnum("tierThreeApiaryOutput", ApiaryOutputs.BLOCK, ApiaryOutputs.COMB, ApiaryOutputs.BLOCK);
            T3_APIARY_QUANTITY = COMMON_BUILDER.comment("\nTier 3 Apiary Output Quantity")
                    .defineInRange("tierThreeApiaryQuantity", 4, 1, Integer.MAX_VALUE);
            T4_APIARY_OUTPUT = COMMON_BUILDER.comment("\nTier 4 Apiary Output")
                    .defineEnum("tierFourApiaryOutput", ApiaryOutputs.BLOCK, ApiaryOutputs.COMB, ApiaryOutputs.BLOCK);
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
            BEE_FLOWER_FOREST_MULTIPLIER = COMMON_BUILDER.comment("The value added to weight for bees in a flower forests")
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
                    .define("beeDiesInVoid", true); //TODO 1.17 change to "beesDieInVoid" Also change comment above to reflect y level changes
            MANUAL_MODE = COMMON_BUILDER.comment("\nThis is an experimental setting. Using this setting means bees will need to be told by the player which flower and hive to use.",
                    "Bees will not scan surroundings for flowers or hives and will instead go to their designated spot until changed.",
                    "WARNING: For now, this will prevent bees from having their wander goal attached which effectively makes them dumb (seriously, they'll just hover in one spot), however it would also significantly improve performance until pathfinding can be optimized.")
                    .define("use_experimental_manual_mode", false);
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
            HONEY_BLOCK_RECIPES = COMMON_BUILDER.comment("\nShould honey block recipes be generated? [true/false]")
                    .define("honeyBlockRecipes", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Mod Options");
            BYPASS_PERFORMANT_CHECK = COMMON_BUILDER.comment("Set this to true if you wish to live life on the edge!", "Seriously though it is recommended this only be used for testing purposes!!!")
                    .define("bypassPerformantCheck", false);
            COMMON_BUILDER.pop();

            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }

    public static class ClientConfig {

        private ClientConfig() {
            throw new IllegalStateException(ModConstants.UTILITY_CLASS);
        }

        public static final ForgeConfigSpec CLIENT_CONFIG;


        static {
            Builder CLIENT_BUILDER = new Builder();

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