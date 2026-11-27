package com.teamresourceful.resourcefulbees_test.api;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.blocks.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public final class CustomHoneycombApiTests {

    private static final Identifier COMB_ID = ModIdentifier.of("gametest_java");

    private static final Identifier ITEM_ID = Identifier.parse("resourcefulbees:gametest_java_honeycomb");

    private static final Identifier BLOCK_ID = Identifier.parse("resourcefulbees:gametest_java_honeycomb_block");

    private CustomHoneycombApiTests() {
    }

    private static OutputVariation variation(GameTestHelper helper) {
        OutputVariation variation = HoneycombRegistry.get().getHoneycombVariation(COMB_ID);

        helper.assertTrue(variation != null, "Custom honeycomb 'gametest_java' was not registered");

        return variation;
    }


    public static void testJavaHoneycombRegistration(GameTestHelper helper) {
        Identifier combId = ModIdentifier.of("gametest_java_honeycomb");

        Identifier blockId = ModIdentifier.of("gametest_java_honeycomb_block");

        Item comb = BuiltInRegistries.ITEM.getValue(combId);
        Block block = BuiltInRegistries.BLOCK.getValue(blockId);

        helper.assertTrue(comb instanceof CustomHoneycombItem, "Java-registered honeycomb item is missing");

        helper.assertTrue(block instanceof HoneycombBlock, "Java-registered honeycomb block is missing");

        helper.succeed();
    }

    public static void customHoneycombVariationRegistered(GameTestHelper helper) {
        HoneycombRegistry registry = HoneycombRegistry.get();

        helper.assertTrue(registry.containsVariation(COMB_ID), "Registry should contain custom honeycomb");

        helper.assertTrue(registry.getVariationIDs().contains(COMB_ID), "Honeycomb types should include gametest");

        helper.assertTrue(COMB_ID.equals(variation(helper).id()), "Registered variation has incorrect identifier");

        helper.succeed();
    }

    public static void customHoneycombItemsRegistered(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(ITEM_ID), "Custom honeycomb item was not registered");

        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(BLOCK_ID), "Custom honeycomb block item was not registered");

        helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(BLOCK_ID), "Custom honeycomb block was not registered");

        helper.succeed();
    }

    public static void customHoneycombHiveOutputs(GameTestHelper helper) {
        OutputVariation variation = variation(helper);
        Item expected = BuiltInRegistries.ITEM.getValue(ITEM_ID);

        int tierNumber = 1;

        for (BeehiveTier tier : BeehiveTier.values()) {
            ItemStack output = variation.getHiveOutput(tier);

            helper.assertTrue(output.is(expected), "Incorrect hive output item for " + tier.id());

            helper.assertTrue(output.getCount() == tierNumber, "Incorrect hive output count for " + tier.id() + ": expected " + tierNumber + ", got " + output.getCount());

            tierNumber++;
        }

        helper.assertTrue(tierNumber == 5, "Expected four registered beehive tiers");

        helper.succeed();
    }

    public static void javaHoneycombApiaryOutputs(GameTestHelper helper) {
        OutputVariation variation = variation(helper);

        int tierNumber = 1;

        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack output = variation.getApiaryOutput(tier);

            Identifier expectedId = tierNumber <= 2 ? ITEM_ID : BLOCK_ID;

            Item expected = BuiltInRegistries.ITEM.getValue(expectedId);

            helper.assertTrue(output.is(expected), "Incorrect apiary output item for " + tier.id());

            helper.assertTrue(output.getCount() == tierNumber, "Incorrect apiary output count for " + tier.id() + ": expected " + tierNumber + ", got " + output.getCount());

            tierNumber++;
        }

        helper.assertTrue(tierNumber == 5, "Expected four registered apiary tiers");

        helper.succeed();
    }

    public static void jsonHoneycombApiaryOutputs(GameTestHelper helper) {
        OutputVariation variation = HoneycombRegistry.get().getHoneycombVariation(Identifier.parse("resourcefulbees_test:coal"));

        helper.assertTrue(variation != null, "Custom honeycomb 'gametest_java' was not registered");

        int tierNumber = 1;

        for (ApiaryTier tier : ApiaryTier.values()) {
            ItemStack output = variation.getApiaryOutput(tier);

            Identifier expectedId = Identifier.parse(tierNumber <= 2 ? "resourcefulbees_test:gametest_json_honeycomb" : "resourcefulbees_test:gametest_json_honeycomb_block");

            Item expected = BuiltInRegistries.ITEM.getValue(expectedId);

            helper.assertTrue(output.is(expected), "Incorrect apiary output item for " + tier.id());

            helper.assertTrue(output.getCount() == tierNumber, "Incorrect apiary output count for " + tier.id() + ": expected " + tierNumber + ", got " + output.getCount());

            tierNumber++;
        }

        helper.assertTrue(tierNumber == 5, "Expected four registered apiary tiers");

        helper.succeed();
    }

    public static void customHoneycombUnknownLookup(GameTestHelper helper) {
        HoneycombRegistry registry = HoneycombRegistry.get();

        helper.assertTrue(!registry.containsVariation(ModIdentifier.of("nonexistent_gametest")), "Unknown honeycomb should not exist");

        helper.assertTrue(registry.getHoneycombVariation(ModIdentifier.of("nonexistent_gametest")) == null, "Unknown honeycomb lookup should return null");

        helper.succeed();
    }

    public static void customHoneycombRegistryReadOnly(GameTestHelper helper) {
        Map<Identifier, OutputVariation> data = HoneycombRegistry.get().getVariationData();

        boolean rejected = false;

        try {
            data.clear();
        } catch (UnsupportedOperationException exception) {
            rejected = true;
        }

        helper.assertTrue(rejected, "Honeycomb registry data should be unmodifiable");

        helper.assertTrue(HoneycombRegistry.get().containsVariation(COMB_ID), "Registry should remain intact");

        helper.succeed();
    }

    public static void customHoneycombOutputCreatesFreshStacks(GameTestHelper helper) {
        OutputVariation variation = variation(helper);

        BeehiveTier tier = BeehiveTier.getOrThrow(Identifier.parse("resourcefulbees:t1"));

        ItemStack first = variation.getHiveOutput(tier);
        ItemStack second = variation.getHiveOutput(tier);

        helper.assertTrue(first != second, "Output should create a new ItemStack");

        first.setCount(32);

        helper.assertTrue(second.getCount() == 1, "Mutating one output should not affect another");

        helper.assertTrue(variation.getHiveOutput(tier).getCount() == 1, "Mutating an output should not affect its template");

        helper.succeed();
    }
}
