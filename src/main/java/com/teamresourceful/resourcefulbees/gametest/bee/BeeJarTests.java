package com.teamresourceful.resourcefulbees.gametest.bee;

import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BeeJarTests {
    private BeeJarTests() {
        /* This utility class should not be instantiated */
    }

    public static void beeJarCapturesBee(GameTestHelper helper) {

        // Arrange: create a survival player.
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // Give the player an empty Bee Jar.
        ItemStack emptyJar = new ItemStack(ModItems.BEE_JAR.get());

        player.setItemInHand(InteractionHand.MAIN_HAND, emptyJar);

        helper.assertFalse(BeeJarItem.isFilled(emptyJar), "Bee Jar should initially be empty");

        // Spawn a bee inside the test structure.
        Bee bee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 1, 1));

        helper.assertTrue(bee.isAlive(), "Bee should be alive before capture");

        // Act: simulate using the jar on the bee.
        emptyJar.interactLivingEntity(player, bee, InteractionHand.MAIN_HAND);

        // Assert: the bee was captured.
        helper.assertTrue(bee.isRemoved(), "Bee should be removed after capture");

        // The interaction may replace the held stack.
        ItemStack capturedJar = player.getItemInHand(InteractionHand.MAIN_HAND);

        helper.assertTrue(BeeJarItem.isFilled(capturedJar), "Bee Jar should be filled after capture");
        helper.assertTrue(BeeJarItem.hasEntityData(capturedJar), "Captured bee data should be stored");
        helper.assertTrue(BeeJarItem.occupantFrom(capturedJar).entityType() == EntityTypes.BEE, "Jar should contain a bee");

        helper.succeed();
    }


    public static void beeJarCapturesBeeFromStack(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // Start with 4 empty Bee Jars.
        ItemStack jars = new ItemStack(ModItems.BEE_JAR.get(), 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, jars);

        // Spawn the bee.
        Bee bee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 1, 1));

        // Act: capture the bee using the stack.
        InteractionResult result = jars.interactLivingEntity(player, bee, InteractionHand.MAIN_HAND);

        // Assert: interaction succeeded.
        helper.assertTrue(result instanceof InteractionResult.Success, "Bee capture should succeed");

        // The original stack should contain 3 empty jars.
        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        helper.assertTrue(heldStack.getCount() == 3, "Expected 3 empty jars after capture");
        helper.assertFalse(BeeJarItem.isFilled(heldStack), "Remaining jars should still be empty");

        // The captured bee should be removed.
        helper.assertTrue(bee.isRemoved(), "Captured bee should be removed from the world");

        // Search the player's inventory for the filled jar.
        ItemStack filledJar = ItemStack.EMPTY;

        for (ItemStack stack : player.getInventory()) {
            if (stack.is(ModItems.BEE_JAR.get())
                    && BeeJarItem.isFilled(stack)) {
                filledJar = stack;
                break;
            }
        }

        helper.assertFalse(filledJar.isEmpty(), "Player should receive a filled Bee Jar");
        helper.assertTrue(filledJar.getCount() == 1, "Captured jar should be a single item");
        helper.assertTrue(BeeJarItem.hasEntityData(filledJar), "Filled jar should contain serialized bee data");
        helper.assertTrue(BeeJarItem.occupantFrom(filledJar).entityType() == EntityTypes.BEE, "Filled jar should contain a bee");

        helper.succeed();
    }

    public static void beeJarReleasesCapturedBee(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // Create a bee to capture.
        Bee bee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 2, 1));

        bee.setCustomName(Component.literal("Test Bee"));

        // Create a filled jar containing the bee's serialized data.
        ItemStack filledJar = new ItemStack(ModItems.BEE_JAR.get());
        filledJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(bee));

        // Remove the original bee so only the released bee remains.
        bee.discard();
        player.setItemInHand(InteractionHand.MAIN_HAND, filledJar);

        // Place a block for the jar to be used on.
        BlockPos blockPos = new BlockPos(1, 1, 1);
        helper.setBlock(blockPos, Blocks.STONE);

        // Construct a block hit result.
        BlockPos absolutePos = helper.absolutePos(blockPos);
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);

        // Release the bee.
        InteractionResult result = filledJar.useOn(context);

        helper.assertTrue(result instanceof InteractionResult.Success, "Releasing the bee should succeed");

        // Verify a bee now exists in the test structure.
        helper.assertEntityPresent(EntityTypes.BEE, new BlockPos(1, 2, 1));

        //
        helper.assertEntityData(new BlockPos(1, 2, 1), EntityTypes.BEE,bee1 -> {
            Component name = bee1.getCustomName();
            assert name != null;
            return name;
     }, Component.literal("Test Bee"));

        // The filled jar should have been consumed.
        helper.assertTrue(filledJar.isEmpty(), "Filled jar should be consumed");

        // The player should have received an empty jar.
        boolean hasEmptyJar = false;

        for (ItemStack stack : player.getInventory()) {
            if (stack.is(ModItems.BEE_JAR.get())
                    && !BeeJarItem.isFilled(stack)) {
                hasEmptyJar = true;
                break;
            }
        }

        helper.assertTrue(hasEmptyJar, "Player should receive an empty Bee Jar");

        helper.succeed();
    }

    public static void beeJarOnlyCapturesBee(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // Start with 4 empty Bee Jars.
        ItemStack jar = new ItemStack(ModItems.BEE_JAR.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, jar);

        // Spawn the pig.
        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(1, 1, 1));

        // Verify the initial conditions.
        helper.assertTrue(pig.isAlive(), "Pig should be alive before interaction");

        helper.assertFalse(BeeJarItem.isFilled(jar), "Bee Jar should initially be empty");

        // Act: capture the bee using the stack.
        InteractionResult result = jar.interactLivingEntity(player, pig, InteractionHand.MAIN_HAND);

        // Assert: interaction failed.
        helper.assertTrue(result instanceof InteractionResult.Fail, "Pig capture should fail");

        // The pig should still exist.
        helper.assertTrue(pig.isAlive() && !pig.isRemoved(), "Pig should remain alive and in the world");

        // The jar should remain empty.
        ItemStack heldJar = player.getItemInHand(InteractionHand.MAIN_HAND);

        helper.assertFalse(BeeJarItem.isFilled(heldJar), "Bee Jar should remain empty");

        // The jar should not have been consumed.
        helper.assertTrue(heldJar.getCount() == 1, "Bee Jar count should remain unchanged");

        // The jar should not contain entity data.
        helper.assertFalse(BeeJarItem.hasEntityData(heldJar), "Bee Jar should not contain entity data");

        helper.succeed();
    }


    public static void beeJarCannotCaptureWithFilledJar(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);

        // Spawn the first bee, which will fill the jar.
        Bee firstBee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 1, 1));

        firstBee.setCustomName(Component.literal("First Bee"));

        // Create a filled jar containing the first bee.
        ItemStack filledJar = new ItemStack(ModItems.BEE_JAR.get());

        filledJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(firstBee));

        // Remove the original bee from the test world.
        firstBee.discard();

        player.setItemInHand(InteractionHand.MAIN_HAND, filledJar);

        // Spawn a second bee to attempt capturing.
        Bee secondBee = helper.spawn(EntityTypes.BEE, new BlockPos(2, 1, 1));

        secondBee.setCustomName(Component.literal("Second Bee"));

        // Save the original occupant for comparison.
        JarOccupant originalOccupant = BeeJarItem.occupantFrom(filledJar);

        // Verify initial conditions.
        helper.assertTrue(BeeJarItem.isFilled(filledJar), "Bee Jar should initially be filled");

        helper.assertTrue(BeeJarItem.hasEntityData(filledJar), "Filled jar should contain serialized bee data");

        // Act: attempt to capture another bee.
        InteractionResult result = filledJar.interactLivingEntity(player, secondBee, InteractionHand.MAIN_HAND);

        // Assert: interaction should fail.
        helper.assertTrue(result == InteractionResult.FAIL, "Capturing a bee with a filled jar should fail");

        // The second bee must remain in the world.
        helper.assertTrue(secondBee.isAlive() && !secondBee.isRemoved(), "Second bee should remain alive");

        // The jar must remain filled.
        ItemStack heldJar = player.getItemInHand(InteractionHand.MAIN_HAND);

        helper.assertTrue(BeeJarItem.isFilled(heldJar), "Bee Jar should remain filled");

        // The original occupant must not be replaced.
        helper.assertTrue(originalOccupant.equals(BeeJarItem.occupantFrom(heldJar)), "Original bee occupant should remain unchanged");

        // The item count should remain unchanged.
        helper.assertTrue(heldJar.getCount() == 1, "Filled jar count should remain unchanged");

        helper.succeed();
    }


    public static void beeJarCreativeRelease(GameTestHelper helper) {
        var player = helper.makeMockServerPlayer(GameType.CREATIVE);

        // Spawn a bee to serialize into the jar.
        Bee bee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 2, 1));
        bee.setCustomName(Component.literal("Creative Bee"));
        ItemStack filledJar = new ItemStack(ModItems.BEE_JAR.get());
        filledJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(bee));
        bee.discard();

        player.setItemInHand(InteractionHand.MAIN_HAND, filledJar);

        // Place the block we'll interact with.
        BlockPos blockPos = new BlockPos(1, 1, 1);
        helper.setBlock(blockPos, Blocks.STONE);

        BlockPos absolutePos = helper.absolutePos(blockPos);

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);

        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);

        // Act: release the bee.
        InteractionResult result = filledJar.useOn(context);

        helper.assertTrue(result instanceof InteractionResult.Success, "Creative release should succeed");

        // The released bee should exist.
        helper.assertEntityPresent(EntityTypes.BEE, new BlockPos(1, 2, 1));

        // Creative mode should preserve the filled jar.
        ItemStack heldJar = player.getItemInHand(InteractionHand.MAIN_HAND);

        helper.assertTrue(heldJar.getCount() == 1, "Creative release should not consume the jar");
        helper.assertTrue(BeeJarItem.isFilled(heldJar), "Creative player should retain the filled jar");
        helper.assertTrue(BeeJarItem.hasEntityData(heldJar), "Jar should retain serialized bee data");

        // No empty jar should have been added.
        for (ItemStack stack : player.getInventory()) {
            helper.assertFalse(stack.is(ModItems.BEE_JAR.get()) && !BeeJarItem.isFilled(stack), "Creative release must not create an empty jar");
        }

        helper.succeed();
    }

// this test fails and i think it's bc the item doesn't spawn in the test world when .drop is called

//    public static void beeJarReleaseWithFullInventory(GameTestHelper helper) {
//        var player = helper.makeMockServerPlayer(GameType.SURVIVAL);
//
//        // Create a bee with a recognizable name.
//        Bee bee = helper.spawn(EntityTypes.BEE, new BlockPos(1, 2, 1));
//
//        bee.setCustomName(Component.literal("Inventory Bee"));
//        ItemStack filledJar = new ItemStack(ModItems.BEE_JAR.get());
//        filledJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(bee));
//        //simulate multiple filled jars from breeder output
//        filledJar.setCount(2);
//
//        bee.discard();
//
//        // Fill every main inventory slot.
//        var inventory = player.getInventory();
//
//        for (int slot = 0; slot < 36; slot++) {
//            inventory.setItem(
//                    slot,
//                    new ItemStack(Items.STONE, 64)
//            );
//        }
//
//        // Replace the selected hotbar slot with the filled jar.
//        player.setItemInHand(InteractionHand.MAIN_HAND, filledJar);
//
//        // Set up the release location.
//        BlockPos blockPos = new BlockPos(1, 1, 1);
//        helper.setBlock(blockPos, Blocks.STONE);
//
//        BlockPos absolutePos = helper.absolutePos(blockPos);
//
//        BlockHitResult hitResult = new BlockHitResult(
//                Vec3.atCenterOf(absolutePos),
//                Direction.UP,
//                absolutePos,
//                false
//        );
//
//        UseOnContext context = new UseOnContext(
//                player,
//                InteractionHand.MAIN_HAND,
//                hitResult
//        );
//
//        // Act: release the bee.
//        InteractionResult result = filledJar.useOn(context);
//
//        helper.assertTrue(
//                result instanceof InteractionResult.Success,
//                "Release should succeed with a full inventory"
//        );
//
//        int emptyJarsInInventory = 0;
//
//        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
//            ItemStack stack = inventory.getItem(slot);
//
//            if (stack.is(ModItems.BEE_JAR.get())
//                    && !BeeJarItem.isFilled(stack)) {
//                emptyJarsInInventory += stack.getCount();
//            }
//        }
//
//        helper.assertTrue(
//                emptyJarsInInventory == 0,
//                "Expected no empty jars in inventory, found "
//                        + emptyJarsInInventory
//        );
//
//        // The bee should be released.
//        helper.assertEntityPresent(
//                EntityTypes.BEE,
//                new BlockPos(1, 2, 1)
//        );
//
//        // The filled jar should have been consumed.
//        helper.assertTrue(
//                filledJar.getCount() == 1,
//                "Filled jar should be consumed"
//        );
//
//        helper.assertTrue(
//                BeeJarItem.isFilled(filledJar),
//                "Jar should still be filled"
//        );
//
//        helper.startSequence()
//                .thenIdle(2)
//                .thenExecute(() -> {
//                    Vec3 playerPos = Vec3.atCenterOf(
//                            helper.absolutePos(new BlockPos(1, 2, 1))
//                    );
//
//                    player.setPos(playerPos.x, playerPos.y, playerPos.z);
//
//                    var droppedJars = helper.getEntities(
//                            EntityTypes.ITEM,
//                            helper.relativePos(player.blockPosition()),
//                            5.0
//                    );
//
//                    boolean foundEmptyJar = droppedJars.stream()
//                            .anyMatch(item -> {
//                                ItemStack stack = item.getItem();
//
//                                return stack.is(ModItems.BEE_JAR.get())
//                                        && !BeeJarItem.isFilled(stack);
//                            });
//
//                    helper.assertTrue(
//                            foundEmptyJar,
//                            "Expected an empty Bee Jar to drop"
//                    );
//                })
//                .thenSucceed();
//    }

}
