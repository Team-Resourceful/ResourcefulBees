
package com.teamresourceful.resourcefulbees.gametest;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.gametest.bee.BeeJarTests;
import com.teamresourceful.resourcefulbees.gametest.block.BreederBlockEntityTests;
import com.teamresourceful.resourcefulbees.gametest.block.TieredBeehiveBlockTests;
import com.teamresourceful.resourcefulbees.gametest.block.TieredBeehiveEntityTests;
import com.teamresourceful.resourcefulbees.gametest.recipes.BreederTests;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Consumer;

public final class ModGameTest {

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS = DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, ModConstants.MOD_ID);

    // Bee Jar Tests
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_CAPTURES_BEE = TEST_FUNCTIONS.register("bee_jar_captures_bee", () -> BeeJarTests::beeJarCapturesBee);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_CAPTURES_BEE_FROM_STACK = TEST_FUNCTIONS.register("bee_jar_captures_bee_from_stack", () -> BeeJarTests::beeJarCapturesBeeFromStack);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_RELEASES_CAPTURED_BEE = TEST_FUNCTIONS.register("bee_jar_releases_captured_bee", () -> BeeJarTests::beeJarReleasesCapturedBee);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_ONLY_CAPTURES_BEE = TEST_FUNCTIONS.register("bee_jar_only_captures_bee", () -> BeeJarTests::beeJarOnlyCapturesBee);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_CANNOT_CAPTURE_WHILE_FILLED = TEST_FUNCTIONS.register("bee_jar_cannot_capture_while_filled", () -> BeeJarTests::beeJarCannotCaptureWithFilledJar);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_CREATIVE_RELEASE = TEST_FUNCTIONS.register("bee_jar_creative_release", () -> BeeJarTests::beeJarCreativeRelease);
    //public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEE_JAR_FULL_INVENTORY_RELEASE_FUNCTION = TEST_FUNCTIONS.register("bee_jar_full_inventory_release", () -> BeeJarTests::beeJarReleaseWithFullInventory);

    //Breeder recipe tests
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_MATCHES_PARENTS = TEST_FUNCTIONS.register("breeder_matches_parents", () -> BreederTests::breederRecipeMatchesParents);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_MATCHES_SWAPPED_PARENTS = TEST_FUNCTIONS.register("breeder_matches_swapped_parents", () -> BreederTests::breederRecipeMatchesSwappedParents);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_REQUIRES_OPTIONAL_INPUT = TEST_FUNCTIONS.register("breeder_requires_optional_input", () -> BreederTests::breederRecipeRequiresOptionalInput);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_JSON_DECODE = TEST_FUNCTIONS.register("breeder_json_decode", () -> BreederTests::breederRecipeJsonDecode);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_NETWORK_ENCODE = TEST_FUNCTIONS.register("breeder_network_encode", () -> BreederTests::breederRecipeNetworkEncode);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_NETWORK_ROUND_TRIP = TEST_FUNCTIONS.register("breeder_network_round_trip", () -> BreederTests::breederRecipeNetworkRoundTrip);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_JSON_ROUND_TRIP = TEST_FUNCTIONS.register("breeder_json_round_trip", () -> BreederTests::breederRecipeJsonRoundTrip);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_NETWORK_PRESERVES_OUTPUTS = TEST_FUNCTIONS.register("breeder_network_preserves_outputs", () -> BreederTests::breederRecipeNetworkPreservesOutputs);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_REJECTS_INVALID_TIME = TEST_FUNCTIONS.register("breeder_rejects_invalid_time", () -> BreederTests::breederRecipeRejectsInvalidTime);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_DEFAULTS_MISSING_TIME = TEST_FUNCTIONS.register("breeder_defaults_missing_time", () -> BreederTests::breederRecipeDefaultsMissingTime);

    //breeder block entity tests
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INV_INSERT_EXTRACT = TEST_FUNCTIONS.register("breeder_inv_insert_extract", () -> BreederBlockEntityTests::breederInventoryInsertExtract);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_REJECTS_OUTPUT_INSERTION = TEST_FUNCTIONS.register("breeder_rejects_output_insertion", () -> BreederBlockEntityTests::breederRejectsOutputInsertion);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INV_LANES_INDEPENDENT = TEST_FUNCTIONS.register("breeder_inv_lanes_independent", () -> BreederBlockEntityTests::breederInventoryLanesIndependent);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_DETECTS_VALID_RECIPE = TEST_FUNCTIONS.register("breeder_detects_valid_recipe", () -> BreederBlockEntityTests::breederDetectsValidRecipe);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_PROCESSES_RECIPE = TEST_FUNCTIONS.register("breeder_processes_recipe", () -> BreederBlockEntityTests::breederProcessesRecipe);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INSUFFICIENT_FEED = TEST_FUNCTIONS.register("breeder_insufficient_feed", () -> BreederBlockEntityTests::breederInsufficientFeed);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INVALIDATES_RECIPE = TEST_FUNCTIONS.register("breeder_invalidates_recipe", () -> BreederBlockEntityTests::breederInvalidatesRecipe);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_SWAPPED_PARENTS = TEST_FUNCTIONS.register("breeder_swapped_parents", () -> BreederBlockEntityTests::breederSwappedParents);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INDEPENDENT_PROCESSING = TEST_FUNCTIONS.register("breeder_independent_processing", () -> BreederBlockEntityTests::breederIndependentProcessing);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_FULL_OUTPUT = TEST_FUNCTIONS.register("breeder_full_output", () -> BreederBlockEntityTests::breederFullOutputInventory);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_PROCESSING_INTERRUPTION = TEST_FUNCTIONS.register("breeder_processing_interruption", () -> BreederBlockEntityTests::breederProcessingInterruption);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_UPGRADE_REDUCES_TIME = TEST_FUNCTIONS.register("breeder_upgrade_reduces_time", () -> BreederBlockEntityTests::breederUpgradeReducesTime);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_UPGRADE_DURING_PROCESSING = TEST_FUNCTIONS.register("breeder_upgrade_during_processing", () -> BreederBlockEntityTests::breederUpgradeDuringProcessing);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_PRODUCES_RETURN_ITEMS = TEST_FUNCTIONS.register("breeder_produces_return_itmes", () -> BreederBlockEntityTests::breederProducesReturnItems);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_RETURN_ITEMS_ROLLBACK = TEST_FUNCTIONS.register("breeder_return_items_rollback", () -> BreederBlockEntityTests::breederReturnItemsRollback);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_INDEPENDENT_RETURN_ITEMS = TEST_FUNCTIONS.register("breeder_independent_return_items", () -> BreederBlockEntityTests::breederIndependentReturnItems);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_RESUMES_AFTER_RELOAD = TEST_FUNCTIONS.register("breeder_resumes_after_reload", () -> BreederBlockEntityTests::breederResumesAfterReload);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BREEDER_DUAL_LANE_RELOAD = TEST_FUNCTIONS.register("breeder_dual_lane_reload", () -> BreederBlockEntityTests::breederDualLaneReload);

    //tiered beehive tests
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_SCRAPER_HARVESTS_SINGLE_COMB = TEST_FUNCTIONS.register("beehive_scraper_harvests_single_comb", () -> TieredBeehiveBlockTests::scraperHarvestsSingleComb);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_SHEARS_HARVEST_ALL_COMBS = TEST_FUNCTIONS.register("beehive_shears_harvest_all_combs", () -> TieredBeehiveBlockTests::shearsHarvestAllCombs);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_SCRAPER_HARVESTS_LAST_COMB = TEST_FUNCTIONS.register("beehive_scraper_harvests_last_comb", () -> TieredBeehiveBlockTests::scraperHarvestsLastComb);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_REQUIRES_FULL_HONEY_LEVEL = TEST_FUNCTIONS.register("beehive_requires_full_honey_level", () -> TieredBeehiveBlockTests::harvestRequiresFullHoneyLevel);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_SHEARS_LOSE_DURABILITY_ON_HARVEST = TEST_FUNCTIONS.register("beehive_shears_lose_durability_on_harvest", () -> TieredBeehiveBlockTests::shearsLoseDurabilityOnHarvest);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_INVALID_TOOL_DOES_NOT_HARVEST = TEST_FUNCTIONS.register("beehive_invalid_tool_does_not_harvest", () -> TieredBeehiveBlockTests::invalidToolDoesNotHarvest);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_SEQUENTIAL_SCRAPER_HARVESTING = TEST_FUNCTIONS.register("beehive_sequential_scraper_harvesting", () -> TieredBeehiveBlockTests::sequentialScraperHarvesting);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_BEE_ENTERS_HIVE = TEST_FUNCTIONS.register("beehive_bee_enters_hive", () -> TieredBeehiveEntityTests::beeEntersHive);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_RESPECTS_BEE_CAPACITY = TEST_FUNCTIONS.register("beehive_respects_bee_capacity", () -> TieredBeehiveEntityTests::hiveRespectsBeeCapacity);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_BEE_RELEASED_FROM_HIVE = TEST_FUNCTIONS.register("beehive_bee_released_from_hive", () -> TieredBeehiveEntityTests::beeReleasedFromHive);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("beehive_occupant_survives_reload", () -> TieredBeehiveEntityTests::hiveOccupantsSurviveReload);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_RELEASED_BEE_EXISTS_IN_WORLD = TEST_FUNCTIONS.register("beehive_released_bee_exists_in_world", () -> TieredBeehiveEntityTests::releasedBeeExistsInWorld);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_BEE_REMAINS_STORED_BEFORE_RELEASE = TEST_FUNCTIONS.register("beehive_bee_remains_stored_before_release", () -> TieredBeehiveEntityTests::beeRemainsStoredBeforeRelease);
    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> BEEHIVE_MULTIPLE_BEES_RELEASE_CORRECTLY = TEST_FUNCTIONS.register("beehive_multiple_bees_release_correctly", () -> TieredBeehiveEntityTests::multipleBeesReleaseCorrectly);

    //apiary tests
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_BEE_ENTERS_APIARY = TEST_FUNCTIONS.register("apiary_bee_enters_apiary", () -> ApiaryBlockEntityTests::beeEntersApiary);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_RESPECTS_BEE_CAPACITY = TEST_FUNCTIONS.register("apiary_respects_bee_capacity", () -> ApiaryBlockEntityTests::apiaryRespectsCapacity);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_BEE_RELEASE = TEST_FUNCTIONS.register("apiary_bee_release", () -> ApiaryBlockEntityTests::apiaryBeeRelease);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("apiary_occupant_survives_reload", () -> ApiaryBlockEntityTests::apiaryOccupantsSurviveReload);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("apiary_does_not_release_through_blocked_exit", () -> ApiaryBlockEntityTests::apiaryDoesNotReleaseThroughBlockedExit);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("apiary_accepts_resource_output", () -> ApiaryBlockEntityTests::apiaryAcceptsResourceOutput);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("apiary_output_survives_reload", () -> ApiaryBlockEntityTests::apiaryOutputSurvivesReload);
//    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> APIARY_OCCUPANT_SURVIVES_RELOAD = TEST_FUNCTIONS.register("apiary_occupant_survives_reload", () -> ApiaryBlockEntityTests::apiaryProducesHoneycombFromNectar);


    private ModGameTest() {}

    public static void register(IEventBus modBus) {
        TEST_FUNCTIONS.register(modBus);
        modBus.addListener(ModGameTest::registerGameTests);
    }

    private static void registerGameTests(RegisterGameTestsEvent event) {
        Identifier environmentId = ModIdentifier.of("bee_tests");

        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(environmentId, new TestEnvironmentDefinition.AllOf(List.of()));

        TestData<Holder<TestEnvironmentDefinition<?>>> data =
                new TestData<>(
                        environment,
                        Level.OVERWORLD, // level
                        ModIdentifier.of("blank"), //structure
                        250,            // Maximum test ticks
                        0,              // Setup ticks
                        true,           // Required
                        Rotation.NONE,  // rotation
                        false,          // Not manual-only
                        1,              // Maximum attempts
                        1,              // Required successes
                        false,          // Sky access
                        1               // padding
                );

        //bee jar tests
        event.registerTest(ModIdentifier.of("bee_jar_captures_bee"), new FunctionGameTestInstance(BEE_JAR_CAPTURES_BEE.key(), data));
        event.registerTest(ModIdentifier.of("bee_jar_captures_bee_from_stack"), new FunctionGameTestInstance(BEE_JAR_CAPTURES_BEE_FROM_STACK.key(), data));
        event.registerTest(ModIdentifier.of("bee_jar_releases_captured_bee"), new FunctionGameTestInstance(BEE_JAR_RELEASES_CAPTURED_BEE.key(), data));
        event.registerTest(ModIdentifier.of("bee_jar_only_captures_bee"), new FunctionGameTestInstance(BEE_JAR_ONLY_CAPTURES_BEE.key(), data));
        event.registerTest(ModIdentifier.of("bee_jar_cannot_capture_while_filled"), new FunctionGameTestInstance(BEE_JAR_CANNOT_CAPTURE_WHILE_FILLED.key(), data));
        event.registerTest(ModIdentifier.of("bee_jar_creative_release"), new FunctionGameTestInstance(BEE_JAR_CREATIVE_RELEASE.key(), data));
        //event.registerTest(ModIdentifier.of("bee_jar_full_inventory_release"), new FunctionGameTestInstance(BEE_JAR_FULL_INVENTORY_RELEASE_FUNCTION.key(), data));

        // breeder recipe tests
        event.registerTest(ModIdentifier.of("breeder_matches_parents"), new FunctionGameTestInstance(BREEDER_MATCHES_PARENTS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_matches_swapped_parents"), new FunctionGameTestInstance(BREEDER_MATCHES_SWAPPED_PARENTS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_requires_optional_input"), new FunctionGameTestInstance(BREEDER_REQUIRES_OPTIONAL_INPUT.key(), data));
        event.registerTest(ModIdentifier.of("breeder_json_decode"), new FunctionGameTestInstance(BREEDER_JSON_DECODE.key(), data));
        event.registerTest(ModIdentifier.of("breeder_network_encode"), new FunctionGameTestInstance(BREEDER_NETWORK_ENCODE.key(), data));
        event.registerTest(ModIdentifier.of("breeder_network_round_trip"), new FunctionGameTestInstance(BREEDER_NETWORK_ROUND_TRIP.key(), data));
        event.registerTest(ModIdentifier.of("breeder_json_round_trip"), new FunctionGameTestInstance(BREEDER_JSON_ROUND_TRIP.key(), data));
        event.registerTest(ModIdentifier.of("breeder_network_preserves_outputs"), new FunctionGameTestInstance(BREEDER_NETWORK_PRESERVES_OUTPUTS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_rejects_invalid_time"), new FunctionGameTestInstance(BREEDER_REJECTS_INVALID_TIME.key(), data));
        event.registerTest(ModIdentifier.of("breeder_defaults_missing_time"), new FunctionGameTestInstance(BREEDER_DEFAULTS_MISSING_TIME.key(), data));

        //breeder block entity tests
        event.registerTest(ModIdentifier.of("breeder_inv_insert_extract"), new FunctionGameTestInstance(BREEDER_INV_INSERT_EXTRACT.key(), data));
        event.registerTest(ModIdentifier.of("breeder_rejects_output_insertion"), new FunctionGameTestInstance(BREEDER_REJECTS_OUTPUT_INSERTION.key(), data));
        event.registerTest(ModIdentifier.of("breeder_inv_lanes_independent"), new FunctionGameTestInstance(BREEDER_INV_LANES_INDEPENDENT.key(), data));
        event.registerTest(ModIdentifier.of("breeder_detects_valid_recipe"), new FunctionGameTestInstance(BREEDER_DETECTS_VALID_RECIPE.key(), data));
        event.registerTest(ModIdentifier.of("breeder_processes_recipe"), new FunctionGameTestInstance(BREEDER_PROCESSES_RECIPE.key(), data));
        event.registerTest(ModIdentifier.of("breeder_insufficient_feed"), new FunctionGameTestInstance(BREEDER_INSUFFICIENT_FEED.key(), data));
        event.registerTest(ModIdentifier.of("breeder_invalidates_recipe"), new FunctionGameTestInstance(BREEDER_INVALIDATES_RECIPE.key(), data));
        event.registerTest(ModIdentifier.of("breeder_swapped_parents"), new FunctionGameTestInstance(BREEDER_SWAPPED_PARENTS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_independent_processing"), new FunctionGameTestInstance(BREEDER_INDEPENDENT_PROCESSING.key(), data));
        event.registerTest(ModIdentifier.of("breeder_full_output"), new FunctionGameTestInstance(BREEDER_FULL_OUTPUT.key(), data));
        event.registerTest(ModIdentifier.of("breeder_processing_interruption"), new FunctionGameTestInstance(BREEDER_PROCESSING_INTERRUPTION.key(), data));
        event.registerTest(ModIdentifier.of("breeder_upgrade_reduces_time"), new FunctionGameTestInstance(BREEDER_UPGRADE_REDUCES_TIME.key(), data));
        event.registerTest(ModIdentifier.of("breeder_upgrade_during_processing"), new FunctionGameTestInstance(BREEDER_UPGRADE_DURING_PROCESSING.key(), data));
        event.registerTest(ModIdentifier.of("breeder_produces_return_items"), new FunctionGameTestInstance(BREEDER_PRODUCES_RETURN_ITEMS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_return_items_rollback"), new FunctionGameTestInstance(BREEDER_RETURN_ITEMS_ROLLBACK.key(), data));
        event.registerTest(ModIdentifier.of("breeder_independent_return_items"), new FunctionGameTestInstance(BREEDER_INDEPENDENT_RETURN_ITEMS.key(), data));
        event.registerTest(ModIdentifier.of("breeder_resumes_after_reload"), new FunctionGameTestInstance(BREEDER_RESUMES_AFTER_RELOAD.key(), data));
        event.registerTest(ModIdentifier.of("breeder_dual_lane_reload"), new FunctionGameTestInstance(BREEDER_DUAL_LANE_RELOAD.key(), data));

        // tiered beehive tests
        event.registerTest(ModIdentifier.of("beehive_scraper_harvests_single_comb"), new FunctionGameTestInstance(BEEHIVE_SCRAPER_HARVESTS_SINGLE_COMB.key(), data));
        event.registerTest(ModIdentifier.of("beehive_shears_harvest_all_combs"), new FunctionGameTestInstance(BEEHIVE_SHEARS_HARVEST_ALL_COMBS.key(), data));
        event.registerTest(ModIdentifier.of("beehive_scraper_harvests_last_comb"), new FunctionGameTestInstance(BEEHIVE_SCRAPER_HARVESTS_LAST_COMB.key(), data));
        event.registerTest(ModIdentifier.of("beehive_requires_full_honey_level"), new FunctionGameTestInstance(BEEHIVE_REQUIRES_FULL_HONEY_LEVEL.key(), data));
        event.registerTest(ModIdentifier.of("beehive_shears_lose_durability_on_harvest"), new FunctionGameTestInstance(BEEHIVE_SHEARS_LOSE_DURABILITY_ON_HARVEST.key(), data));
        event.registerTest(ModIdentifier.of("beehive_invalid_tool_does_not_harvest"), new FunctionGameTestInstance(BEEHIVE_INVALID_TOOL_DOES_NOT_HARVEST.key(), data));
        event.registerTest(ModIdentifier.of("beehive_sequential_scraper_harvesting"), new FunctionGameTestInstance(BEEHIVE_SEQUENTIAL_SCRAPER_HARVESTING.key(), data));
        event.registerTest(ModIdentifier.of("beehive_bee_enters_hive"), new FunctionGameTestInstance(BEEHIVE_BEE_ENTERS_HIVE.key(), data));
        event.registerTest(ModIdentifier.of("beehive_respects_bee_capacity"), new FunctionGameTestInstance(BEEHIVE_RESPECTS_BEE_CAPACITY.key(), data));
        event.registerTest(ModIdentifier.of("beehive_bee_released_from_hive"), new FunctionGameTestInstance(BEEHIVE_BEE_RELEASED_FROM_HIVE.key(), data));
        event.registerTest(ModIdentifier.of("beehive_occupant_survives_reload"), new FunctionGameTestInstance(BEEHIVE_OCCUPANT_SURVIVES_RELOAD.key(), data));
        event.registerTest(ModIdentifier.of("beehive_released_bee_exists_in_world"), new FunctionGameTestInstance(BEEHIVE_RELEASED_BEE_EXISTS_IN_WORLD.key(), data));
        event.registerTest(ModIdentifier.of("beehive_bee_remains_stored_before_release"), new FunctionGameTestInstance(BEEHIVE_BEE_REMAINS_STORED_BEFORE_RELEASE.key(), data));
        event.registerTest(ModIdentifier.of("beehive_multiple_bees_release_correctly"), new FunctionGameTestInstance(BEEHIVE_MULTIPLE_BEES_RELEASE_CORRECTLY.key(), data));

        // apiary tests
//        event.registerTest(ModIdentifier.of("apiary_bee_enters_apiary"), new FunctionGameTestInstance(APIARY_BEE_ENTERS_APIARY.key(), data));
//        event.registerTest(ModIdentifier.of("apiary_respects_bee_capacity"), new FunctionGameTestInstance(APIARY_RESPECTS_BEE_CAPACITY.key(), data));
//        event.registerTest(ModIdentifier.of("apiary_bee_release"), new FunctionGameTestInstance(APIARY_BEE_RELEASE.key(), data));
//        event.registerTest(ModIdentifier.of("apiary_occupant_survives_reload"), new FunctionGameTestInstance(APIARY_OCCUPANT_SURVIVES_RELOAD.key(), data));

    }

}
