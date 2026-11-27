
package com.teamresourceful.resourcefulbees.gametest.block;

import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TieredBeehiveBlockTests {

    private static final BlockPos HIVE_POS = new BlockPos(1, 1, 1);

    private record HiveFixture(TieredBeehiveBlock block, TieredBeehiveBlockEntity entity, BlockPos pos) {}

    private static HiveFixture createHive(GameTestHelper helper) {
        // Choose a registered tiered hive rather than depending
        // on a specific dynamically registered hive name.
        TieredBeehiveBlock block = (TieredBeehiveBlock)
                ModBlocks.HIVES.getEntries()
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .get();

        helper.setBlock(HIVE_POS, block);

        TieredBeehiveBlockEntity entity = helper.getBlockEntity(
                HIVE_POS,
                TieredBeehiveBlockEntity.class
        );

        return new HiveFixture(block, entity, helper.absolutePos(HIVE_POS));
    }

    private static int fillHive(HiveFixture hive) {
        int capacity = hive.block().getTier().maxCombs();

        List<ItemStack> combs = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            combs.add(new ItemStack(Items.HONEYCOMB));
        }

        hive.entity().upgradeHive(combs);
        return capacity;
    }

    private static void interact(GameTestHelper helper, BlockPos pos, Item item) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        ItemStack stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), stack, InteractionHand.MAIN_HAND, hit);
    }

    private static void interact(GameTestHelper helper, ServerPlayer player, BlockPos pos) {
        ItemStack stack = player.getMainHandItem();

        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), stack, InteractionHand.MAIN_HAND, hit);
    }

    private static int countDroppedItems(GameTestHelper helper, BlockPos pos, Item item) {
        AABB area = new AABB(pos).inflate(1.0);

        return helper.getLevel()
                .getEntitiesOfClass(ItemEntity.class, area)
                .stream()
                .filter(entity -> entity.getItem().is(item))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    public static void scraperHarvestsSingleComb(GameTestHelper helper) {
        HiveFixture hive = createHive(helper);
        int initialCombs = fillHive(hive);

        helper.assertTrue(
                initialCombs > 1,
                "Test hive must hold multiple combs"
        );

        helper.assertTrue(
                helper.getLevel().getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 5,
                "Hive should start at honey level 5"
        );

        interact(helper, hive.pos(), ModItems.SCRAPER.get());

        // Harvesting must update the inventory immediately.
        helper.assertTrue(
                hive.entity().numberOfCombs() == initialCombs - 1,
                "Scraper should remove exactly one honeycomb"
        );

        helper.assertTrue(
                helper.getLevel().getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 5,
                "Partial scraper harvest should retain honey level"
        );

        // Allow the server to register spawned item entities.
        helper.runAfterDelay(1, () -> {
            int actual = countDroppedItems(helper, hive.pos(), Items.HONEYCOMB);

            helper.assertTrue(
                    actual == 1,
                    "Expected 1 honeycomb, found " + actual
            );

            helper.succeed();
        });
    }

    public static void shearsHarvestAllCombs(GameTestHelper helper) {
        helper.assertTrue(
                GeneralConfig.allowShears,
                "Shears harvesting must be enabled for this test"
        );

        HiveFixture hive = createHive(helper);
        int initialCombs = fillHive(hive);

        interact(helper, hive.pos(), Items.SHEARS);

        helper.assertTrue(
                hive.entity().numberOfCombs() == 0,
                "Shears should remove all honeycombs"
        );

        helper.assertTrue(
                countDroppedItems(helper, hive.pos(), Items.HONEYCOMB)
                        == initialCombs,
                "Shears should drop every stored honeycomb"
        );

        helper.assertTrue(
                helper.getLevel().getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 0,
                "Empty hive should reset its honey level"
        );

        helper.succeed();
    }

    public static void scraperHarvestsLastComb(GameTestHelper helper) {
        HiveFixture hive = createHive(helper);
        int initialCombs = fillHive(hive);

        // Remove all but one comb directly from the queue.
        // The honey level remains 5 until recalculated.
        for (int i = 1; i < initialCombs; i++) {
            hive.entity().getResourceHoneycomb();
        }

        helper.assertTrue(
                hive.entity().numberOfCombs() == 1,
                "Test setup should leave one honeycomb"
        );

        interact(helper, hive.pos(), ModItems.SCRAPER.get());

        helper.assertTrue(
                hive.entity().numberOfCombs() == 0,
                "Scraper should remove the final honeycomb"
        );

        helper.assertTrue(
                countDroppedItems(helper, hive.pos(), Items.HONEYCOMB) == 1,
                "Final honeycomb should be dropped"
        );

        helper.assertTrue(
                helper.getLevel().getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 0,
                "Hive should reset after the final comb is harvested"
        );

        helper.succeed();
    }

    public static void harvestRequiresFullHoneyLevel(GameTestHelper helper) {
        HiveFixture hive = createHive(helper);

        // Seed the queue, but deliberately leave the block's
        // honey level below the harvesting threshold.
        hive.entity().upgradeHive(
                List.of(new ItemStack(Items.HONEYCOMB))
        );

        helper.getLevel().setBlockAndUpdate(
                hive.pos(),
                helper.getLevel().getBlockState(hive.pos())
                        .setValue(BeehiveBlock.HONEY_LEVEL, 4)
        );

        int initialCombs = hive.entity().numberOfCombs();

        interact(helper, hive.pos(), ModItems.SCRAPER.get());
        interact(helper, hive.pos(), Items.SHEARS);

        helper.assertTrue(
                hive.entity().numberOfCombs() == initialCombs,
                "Tools should not harvest below honey level 5"
        );

        helper.assertTrue(
                countDroppedItems(helper, hive.pos(), Items.HONEYCOMB) == 0,
                "No honeycombs should be dropped"
        );

        helper.assertTrue(
                helper.getLevel().getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 4,
                "Failed harvesting should not change honey level"
        );

        helper.succeed();
    }

    public static void shearsLoseDurabilityOnHarvest(GameTestHelper helper) {
        helper.assertTrue(
                GeneralConfig.allowShears,
                "Shears harvesting must be enabled"
        );

        HiveFixture hive = createHive(helper);
        fillHive(hive);

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        ItemStack shears = new ItemStack(Items.SHEARS);
        player.setItemInHand(InteractionHand.MAIN_HAND, shears);

        int initialDamage = shears.getDamageValue();

        interact(helper, player, hive.pos());

        helper.assertTrue(
                hive.entity().numberOfCombs() == 0,
                "Shears should harvest all honeycombs"
        );

        helper.assertTrue(
                shears.getDamageValue() == initialDamage + 1,
                "Shears should lose exactly one durability"
        );

        helper.succeed();
    }

    public static void invalidToolDoesNotHarvest(GameTestHelper helper) {
        HiveFixture hive = createHive(helper);
        int initialCombs = fillHive(hive);

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        ItemStack stick = new ItemStack(Items.STICK);
        player.setItemInHand(InteractionHand.MAIN_HAND, stick);

        interact(helper, player, hive.pos());

        helper.assertTrue(
                hive.entity().numberOfCombs() == initialCombs,
                "Invalid tool should not remove honeycombs"
        );

        helper.assertTrue(
                countDroppedItems(
                        helper,
                        hive.pos(),
                        Items.HONEYCOMB
                ) == 0,
                "Invalid tool should not drop honeycombs"
        );

        helper.assertTrue(
                helper.getLevel()
                        .getBlockState(hive.pos())
                        .getValue(BeehiveBlock.HONEY_LEVEL) == 5,
                "Invalid tool should not change honey level"
        );

        helper.assertTrue(
                player.getMainHandItem().is(Items.STICK),
                "Player should retain the invalid tool"
        );

        helper.succeed();
    }

    public static void sequentialScraperHarvesting(GameTestHelper helper) {
        HiveFixture hive = createHive(helper);
        int initialCombs = fillHive(hive);

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);

        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.SCRAPER.get())
        );

        for (int harvest = 1; harvest <= initialCombs; harvest++) {
            interact(helper, player, hive.pos());

            int expectedRemaining = initialCombs - harvest;

            helper.assertTrue(
                    hive.entity().numberOfCombs() == expectedRemaining,
                    "Harvest " + harvest
                            + " should leave "
                            + expectedRemaining + " combs"
            );

            helper.assertTrue(
                    countDroppedItems(
                            helper,
                            hive.pos(),
                            Items.HONEYCOMB
                    ) == harvest,
                    "Harvest " + harvest
                            + " should produce exactly "
                            + harvest + " honeycombs"
            );

            int expectedHoneyLevel =
                    expectedRemaining == 0 ? 0 : 5;

            helper.assertTrue(
                    helper.getLevel()
                            .getBlockState(hive.pos())
                            .getValue(BeehiveBlock.HONEY_LEVEL)
                            == expectedHoneyLevel,
                    "Honey level should only reset after final harvest"
            );
        }

        helper.succeed();
    }
}
