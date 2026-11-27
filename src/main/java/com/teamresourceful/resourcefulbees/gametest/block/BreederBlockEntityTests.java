package com.teamresourceful.resourcefulbees.gametest.block;

import com.teamresourceful.resourcefulbees.common.blockentities.BreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BreederBlockEntityTests {

    private static BreederBlockEntity createBreeder(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);

        helper.setBlock(pos, ModBlocks.BREEDER_BLOCK.get());

        return helper.getBlockEntity(
            pos,
            BreederBlockEntity.class
        );
    }

    private static void insert(BreederBlockEntity breeder, int slot, ItemStack stack) {
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = breeder.getInventory().insert(
                slot,
                ItemResource.of(stack),
                stack.getCount(),
                transaction
            );

            if (inserted != stack.getCount()) {
                throw new IllegalStateException(
                    "Could not insert complete stack into slot " + slot
                );
            }

            transaction.commit();
        }
    }


    private static void extract(BreederBlockEntity breeder, int slot, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = breeder.getInventory().extract(
                    slot,
                    breeder.getInventory().getResource(slot),
                    amount,
                    transaction
            );

            if (extracted != amount) {
                throw new IllegalStateException(
                        "Expected to extract " + amount
                                + " items from slot " + slot
                                + ", extracted " + extracted
                );
            }

            transaction.commit();
        }
    }

    private static int countOutput(BreederBlockEntity breeder, Item item) {
        int count = 0;

        for (int slot : BreederConstants.OUTPUT_SLOTS) {
            ItemStack stack = breeder.getItem(slot);

            if (stack.is(item)) {
                count += stack.getCount();
            }
        }

        return count;
    }


    public static void breederInventoryInsertExtract(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        int slot = BreederConstants.FEED_1_SLOTS.getFirst();

        insert(breeder, slot, new ItemStack(Items.WHEAT, 16));

        helper.assertTrue(
                breeder.getItem(slot).is(Items.WHEAT),
                "Feed slot should contain wheat"
        );

        helper.assertTrue(
                breeder.getItem(slot).getCount() == 16,
                "Feed slot should contain 16 wheat"
        );

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = breeder.getInventory().extract(
                    slot,
                    ItemResource.of(Items.WHEAT),
                    6,
                    transaction
            );

            helper.assertTrue(
                    extracted == 6,
                    "Should extract exactly 6 wheat"
            );

            transaction.commit();
        }

        helper.assertTrue(
                breeder.getItem(slot).getCount() == 10,
                "Feed slot should contain 10 wheat after extraction"
        );

        helper.succeed();
    }


    public static void breederRejectsOutputInsertion(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        int slot = BreederConstants.OUTPUT_SLOT_START;

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = breeder.getInventory().insert(
                    slot,
                    ItemResource.of(Items.HONEYCOMB),
                    4,
                    transaction
            );

            helper.assertTrue(
                    inserted == 0,
                    "External insertion into output slots should be rejected"
            );

            transaction.commit();
        }

        helper.assertTrue(
                breeder.getItem(slot).isEmpty(),
                "Output slot should remain empty"
        );

        helper.succeed();
    }

    public static void breederInventoryLanesIndependent(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        int firstFeedSlot = BreederConstants.FEED_1_SLOTS.get(0);
        int secondFeedSlot = BreederConstants.FEED_1_SLOTS.get(1);

        insert(
                breeder,
                firstFeedSlot,
                new ItemStack(Items.WHEAT, 12)
        );

        insert(
                breeder,
                secondFeedSlot,
                new ItemStack(Items.CARROT, 8)
        );

        helper.assertTrue(
                breeder.getItem(firstFeedSlot).is(Items.WHEAT),
                "First breeder should contain wheat"
        );

        helper.assertTrue(
                breeder.getItem(firstFeedSlot).getCount() == 12,
                "First breeder should have 12 wheat"
        );

        helper.assertTrue(
                breeder.getItem(secondFeedSlot).is(Items.CARROT),
                "Second breeder should contain carrots"
        );

        helper.assertTrue(
                breeder.getItem(secondFeedSlot).getCount() == 8,
                "Second breeder should have 8 carrots"
        );

        helper.succeed();
    }

    private static void fillBreederLane(BreederBlockEntity breeder, int lane) {
        insert(
                breeder,
                BreederConstants.PARENT_1_SLOTS.get(lane),
                new ItemStack(Items.STICK)
        );

        insert(
                breeder,
                BreederConstants.FEED_1_SLOTS.get(lane),
                new ItemStack(Items.WHEAT, 10)
        );

        insert(
                breeder,
                BreederConstants.PARENT_2_SLOTS.get(lane),
                new ItemStack(Items.BLAZE_ROD)
        );

        insert(
                breeder,
                BreederConstants.FEED_2_SLOTS.get(lane),
                new ItemStack(Items.CARROT, 10)
        );

        insert(
                breeder,
                BreederConstants.EMPTY_JAR_SLOTS.get(lane),
                new ItemStack(Items.DIAMOND, 4)
        );
    }

    public static void breederDetectsValidRecipe(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillBreederLane(breeder, 0);

        var input = new BreederRecipe.Input(
                breeder.getItem(BreederConstants.PARENT_1_SLOTS.getFirst()),
                breeder.getItem(BreederConstants.FEED_1_SLOTS.getFirst()),
                breeder.getItem(BreederConstants.PARENT_2_SLOTS.getFirst()),
                breeder.getItem(BreederConstants.FEED_2_SLOTS.getFirst()),
                breeder.getItem(BreederConstants.EMPTY_JAR_SLOTS.getFirst())
        );

        var recipe = helper.getLevel().recipeAccess().getRecipeFor(ModRecipes.BREEDER_RECIPE_TYPE.get(), input, helper.getLevel());

        helper.assertTrue(
                recipe.isPresent(),
                "Breeder should find a recipe for valid inputs"
        );

        helper.assertTrue(
                recipe.get().value().time() == 100,
                "GameTest recipe should take 100 ticks"
        );

        helper.succeed();
    }


    public static void breederProcessesRecipe(
            GameTestHelper helper
    ) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillBreederLane(breeder, 0);

        // Recipe time is 100 ticks.
        // Allow a few extra ticks for initialization.
        helper.runAfterDelay(105, () -> {
            int outputCount = 0;

            for (int slot : BreederConstants.OUTPUT_SLOTS) {
                ItemStack stack = breeder.getItem(slot);

                if (stack.is(Items.HONEYCOMB)) {
                    outputCount += stack.getCount();
                }
            }

            helper.assertTrue(
                    outputCount == 1,
                    "Breeder should produce exactly one honeycomb"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_1_SLOTS.getFirst()
                    ).getCount() == 8,
                    "Breeder should consume 2 wheat"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.getFirst()
                    ).getCount() == 7,
                    "Breeder should consume 3 carrots"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.getFirst()
                    ).getCount() == 3,
                    "Breeder should consume one optional input"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.PARENT_1_SLOTS.getFirst()
                    ).is(Items.STICK),
                    "First parent should remain in the breeder"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.PARENT_2_SLOTS.getFirst()
                    ).is(Items.BLAZE_ROD),
                    "Second parent should remain in the breeder"
            );

            helper.succeed();
        });
    }

    public static void breederInsufficientFeed(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillBreederLane(breeder, 0);

        int feedSlot = BreederConstants.FEED_1_SLOTS.getFirst();

        // Reduce wheat from 10 to 1.
        extract(breeder, feedSlot, 9);

        helper.assertTrue(
                breeder.getItem(feedSlot).getCount() == 1,
                "Test setup should contain exactly one wheat"
        );

        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Breeder should not produce output with insufficient feed"
            );

            helper.assertTrue(
                    breeder.getItem(feedSlot).getCount() == 1,
                    "Insufficient feed should not be consumed"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.getFirst()
                    ).getCount() == 4,
                    "Optional input should not be consumed"
            );

            helper.succeed();
        });
    }

    public static void breederInvalidatesRecipe(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillBreederLane(breeder, 0);

        int feedSlot = BreederConstants.FEED_1_SLOTS.getFirst();

        helper.runAfterDelay(20, () -> {
            extract(breeder, feedSlot, 10);

            helper.assertTrue(
                    breeder.getItem(feedSlot).isEmpty(),
                    "Feed slot should be empty after removal"
            );
        });

        helper.runAfterDelay(110, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Breeder should stop after required feed is removed"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.getFirst()
                    ).getCount() == 10,
                    "Other feed should remain untouched"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.getFirst()
                    ).getCount() == 4,
                    "Optional input should remain untouched"
            );

            helper.succeed();
        });
    }

    public static void breederSwappedParents(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        int lane = 0;

        insert(
                breeder,
                BreederConstants.PARENT_1_SLOTS.get(lane),
                new ItemStack(Items.BLAZE_ROD)
        );

        insert(
                breeder,
                BreederConstants.FEED_1_SLOTS.get(lane),
                new ItemStack(Items.CARROT, 10)
        );

        insert(
                breeder,
                BreederConstants.PARENT_2_SLOTS.get(lane),
                new ItemStack(Items.STICK)
        );

        insert(
                breeder,
                BreederConstants.FEED_2_SLOTS.get(lane),
                new ItemStack(Items.WHEAT, 10)
        );

        insert(
                breeder,
                BreederConstants.EMPTY_JAR_SLOTS.get(lane),
                new ItemStack(Items.DIAMOND, 4)
        );

        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 1,
                    "Swapped parents should produce honeycomb"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_1_SLOTS.get(lane)
                    ).getCount() == 7,
                    "Swapped parent 1 should consume 3 carrots"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.get(lane)
                    ).getCount() == 8,
                    "Swapped parent 2 should consume 2 wheat"
            );

            helper.succeed();
        });
    }

    public static void breederIndependentProcessing(
            GameTestHelper helper
    ) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillBreederLane(breeder, 0);
        fillBreederLane(breeder, 1);

        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 2,
                    "Both breeding lanes should produce one honeycomb"
            );

            for (int lane = 0; lane < BreederConstants.BREEDERS; lane++) {
                helper.assertTrue(
                        breeder.getItem(
                                BreederConstants.FEED_1_SLOTS.get(lane)
                        ).getCount() == 8,
                        "Lane " + lane + " should consume 2 wheat"
                );

                helper.assertTrue(
                        breeder.getItem(
                                BreederConstants.FEED_2_SLOTS.get(lane)
                        ).getCount() == 7,
                        "Lane " + lane + " should consume 3 carrots"
                );

                helper.assertTrue(
                        breeder.getItem(
                                BreederConstants.EMPTY_JAR_SLOTS.get(lane)
                        ).getCount() == 3,
                        "Lane " + lane + " should consume one optional input"
                );
            }

            helper.succeed();
        });
    }

    public static void breederFullOutputInventory(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        // Fill all 18 output slots.
        try (Transaction transaction = Transaction.openRoot()) {
            breeder.getInventory().insertOutput(
                    ItemResource.of(Items.COBBLESTONE),
                    BreederConstants.OUTPUT_SLOTS.size() * 64,
                    transaction
            );

            transaction.commit();
        }

        for (int slot : BreederConstants.OUTPUT_SLOTS) {
            helper.assertTrue(
                    breeder.getItem(slot).is(Items.COBBLESTONE)
                            && breeder.getItem(slot).getCount() == 64,
                    "Output slot " + slot + " should be full"
            );
        }

        fillBreederLane(breeder, 0);

        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Full output inventory should not accept honeycomb"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_1_SLOTS.getFirst()
                    ).getCount() == 10,
                    "Wheat should not be consumed when output is full"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.getFirst()
                    ).getCount() == 10,
                    "Carrots should not be consumed when output is full"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.getFirst()
                    ).getCount() == 4,
                    "Optional input should not be consumed when output is full"
            );

            helper.succeed();
        });
    }

    public static void breederProcessingInterruption(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillBreederLane(breeder, 0);

        int feedSlot = BreederConstants.FEED_1_SLOTS.getFirst();

        helper.runAfterDelay(40, () -> extract(breeder, feedSlot, 10));

        helper.runAfterDelay(60, () -> insert(
                breeder,
                feedSlot,
                new ItemStack(Items.WHEAT, 10)
        ));

        helper.runAfterDelay(105, () -> helper.assertTrue(
                countOutput(breeder, Items.HONEYCOMB) == 0,
                "Breeding should restart after interruption"
        ));

        helper.runAfterDelay(165, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 1,
                    "Breeder should complete after restarting"
            );

            helper.assertTrue(
                    breeder.getItem(feedSlot).getCount() == 8,
                    "Breeder should consume 2 wheat"
            );

            helper.succeed();
        });
    }

    public static void breederUpgradeReducesTime(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        // Upgrade slot currently uses the stack count.
        insert(
                breeder,
                BreederConstants.UPGRADE_SLOT,
                new ItemStack(Items.STICK, 2)
        );

        fillBreederLane(breeder, 0);

        helper.runAfterDelay(75, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Breeder should not finish before 80 ticks"
            );
        });

        helper.runAfterDelay(85, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 1,
                    "Two upgrades should reduce processing to 80 ticks"
            );

            helper.succeed();
        });
    }

    public static void breederUpgradeDuringProcessing(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillBreederLane(breeder, 0);

        helper.runAfterDelay(40, () -> {
            insert(
                    breeder,
                    BreederConstants.UPGRADE_SLOT,
                    new ItemStack(Items.STICK, 2)
            );
        });

        helper.runAfterDelay(75, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Breeder should not finish before 80 ticks"
            );
        });

        helper.runAfterDelay(85, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 1,
                    "Upgrade change should affect active processing"
            );

            helper.succeed();
        });
    }

    private static void fillReturnItemLane(BreederBlockEntity breeder, int lane) {
        insert(
                breeder,
                BreederConstants.PARENT_1_SLOTS.get(lane),
                new ItemStack(Items.STICK)
        );

        insert(
                breeder,
                BreederConstants.FEED_1_SLOTS.get(lane),
                new ItemStack(Items.HONEY_BOTTLE, 10)
        );

        insert(
                breeder,
                BreederConstants.PARENT_2_SLOTS.get(lane),
                new ItemStack(Items.BLAZE_ROD)
        );

        insert(
                breeder,
                BreederConstants.FEED_2_SLOTS.get(lane),
                new ItemStack(Items.CARROT, 10)
        );

        insert(
                breeder,
                BreederConstants.EMPTY_JAR_SLOTS.get(lane),
                new ItemStack(Items.DIAMOND, 4)
        );
    }

    public static void breederProducesReturnItems(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillReturnItemLane(breeder, 0);

        helper.runAfterDelay(110, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 1,
                    "Breeder should produce one honeycomb"
            );

            helper.assertTrue(
                    countOutput(breeder, Items.GLASS_BOTTLE) == 2,
                    "Breeder should return two glass bottles"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_1_SLOTS.getFirst()
                    ).getCount() == 8,
                    "Breeder should consume two honey bottles"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.getFirst()
                    ).getCount() == 7,
                    "Breeder should consume three carrots"
            );

            helper.succeed();
        });
    }

    public static void breederReturnItemsRollback(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillReturnItemLane(breeder, 0);

        // Leave exactly one output slot empty.
        for (int i = 0; i < BreederConstants.OUTPUT_SLOTS.size() - 1; i++) {
            breeder.getInventory().set(
                    BreederConstants.OUTPUT_SLOTS.get(i),
                    ItemResource.of(Items.COBBLESTONE),
                    64
            );
        }

        helper.runAfterDelay(110, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 0,
                    "Child output should roll back"
            );

            helper.assertTrue(
                    countOutput(breeder, Items.GLASS_BOTTLE) == 0,
                    "Return items should roll back"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_1_SLOTS.getFirst()
                    ).getCount() == 10,
                    "Honey bottles should not be consumed"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.FEED_2_SLOTS.getFirst()
                    ).getCount() == 10,
                    "Carrots should not be consumed"
            );

            helper.assertTrue(
                    breeder.getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.getFirst()
                    ).getCount() == 4,
                    "Optional input should not be consumed"
            );

            helper.succeed();
        });
    }

    public static void breederIndependentReturnItems(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);

        fillReturnItemLane(breeder, 0);
        fillReturnItemLane(breeder, 1);

        helper.runAfterDelay(110, () -> {
            helper.assertTrue(
                    countOutput(breeder, Items.HONEYCOMB) == 2,
                    "Both lanes should produce honeycomb"
            );

            helper.assertTrue(
                    countOutput(breeder, Items.GLASS_BOTTLE) == 4,
                    "Both lanes should return glass bottles"
            );

            for (int lane = 0; lane < 2; lane++) {
                helper.assertTrue(
                        breeder.getItem(
                                BreederConstants.FEED_1_SLOTS.get(lane)
                        ).getCount() == 8,
                        "Each lane should consume two honey bottles"
                );

                helper.assertTrue(
                        breeder.getItem(
                                BreederConstants.FEED_2_SLOTS.get(lane)
                        ).getCount() == 7,
                        "Each lane should consume three carrots"
                );
            }

            helper.succeed();
        });
    }

    private static BreederBlockEntity reloadBreeder(GameTestHelper helper, BreederBlockEntity original) {
        var level = helper.getLevel();
        BlockPos pos = original.getBlockPos();

        // Serialize the current block entity, including its inventory
        // and breeding progress.
        CompoundTag saved = original.saveWithFullMetadata(
                level.registryAccess()
        );

        // Reconstruct the block entity from its serialized data.
        BlockEntity loaded = BlockEntity.loadStatic(
                pos,
                original.getBlockState(),
                saved,
                level.registryAccess()
        );

        helper.assertTrue(
                loaded instanceof BreederBlockEntity,
                "Saved breeder should deserialize successfully"
        );

        // Replace the live block entity so subsequent ticks use
        // the restored instance.
        level.removeBlockEntity(pos);
        level.setBlockEntity(loaded);

        return (BreederBlockEntity) loaded;
    }

    public static void breederResumesAfterReload(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillBreederLane(breeder, 0);

        final BreederBlockEntity[] current = {breeder};

        helper.runAfterDelay(50, () -> {
            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 0,
                    "Breeder should still be processing before reload"
            );

            current[0] = reloadBreeder(helper, current[0]);

            helper.assertTrue(
                    current[0].getItem(
                            BreederConstants.FEED_1_SLOTS.get(0)
                    ).getCount() == 10,
                    "Reloaded breeder should retain its inventory"
            );
        });

        helper.runAfterDelay(90, () -> {
            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 0,
                    "Breeder should not complete before 100 ticks"
            );
        });

        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 1,
                    "Breeder should resume and finish after reload"
            );

            helper.assertTrue(
                    current[0].getItem(
                            BreederConstants.FEED_1_SLOTS.get(0)
                    ).getCount() == 8,
                    "Reloaded breeder should consume two wheat"
            );

            helper.assertTrue(
                    current[0].getItem(
                            BreederConstants.EMPTY_JAR_SLOTS.get(0)
                    ).getCount() == 3,
                    "Reloaded breeder should consume one optional input"
            );

            helper.succeed();
        });
    }

    public static void breederDualLaneReload(GameTestHelper helper) {
        BreederBlockEntity breeder = createBreeder(helper);
        fillBreederLane(breeder, 0);

        final BreederBlockEntity[] current = {breeder};

        // Start lane 1 thirty ticks later.
        helper.runAfterDelay(30, () -> {
            fillBreederLane(current[0], 1);
        });

        // Reload while both lanes are processing.
        helper.runAfterDelay(55, () -> {
            current[0] = reloadBreeder(helper, current[0]);

            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 0,
                    "Neither lane should have completed at reload"
            );
        });

        // Lane 0 should finish first.
        helper.runAfterDelay(105, () -> {
            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 1,
                    "Only lane 0 should have completed"
            );

            helper.assertTrue(
                    current[0].getItem(
                            BreederConstants.FEED_1_SLOTS.get(0)
                    ).getCount() == 8,
                    "Lane 0 should have consumed its wheat"
            );

            helper.assertTrue(
                    current[0].getItem(
                            BreederConstants.FEED_1_SLOTS.get(1)
                    ).getCount() == 10,
                    "Lane 1 should still be processing"
            );
        });

        // Lane 1 should finish approximately 30 ticks later.
        helper.runAfterDelay(135, () -> {
            helper.assertTrue(
                    countOutput(current[0], Items.HONEYCOMB) == 2,
                    "Both lanes should complete independently"
            );

            for (int lane = 0; lane < BreederConstants.BREEDERS; lane++) {
                helper.assertTrue(
                        current[0].getItem(
                                BreederConstants.FEED_1_SLOTS.get(lane)
                        ).getCount() == 8,
                        "Lane " + lane + " should consume two wheat"
                );

                helper.assertTrue(
                        current[0].getItem(
                                BreederConstants.EMPTY_JAR_SLOTS.get(lane)
                        ).getCount() == 3,
                        "Lane " + lane + " should consume one optional input"
                );
            }

            helper.succeed();
        });
    }

}
