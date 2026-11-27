package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

public final class CourierReturnGameTests {

    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos START = new BlockPos(6, 2, 2);

    private CourierReturnGameTests() {
    }

    private static ColonyControllerBlockEntity controller(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());

        helper.setBlock(CONTROLLER, helper.getBlockState(CONTROLLER).setValue(BeehiveBlock.FACING, Direction.EAST));

        return helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
    }

    private static ResourcefulBee courier(GameTestHelper helper, ColonyControllerBlockEntity controller) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos pos = helper.absolutePos(START);

        bee.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

        bee.setNoAi(false);

        helper.assertTrue(controller.registerCourier(bee), "Failed to register test courier");

        return bee;
    }

    public static void idleCourierReturns(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        ResourcefulBee bee = courier(helper, controller);
        UUID id = bee.getUUID();

        helper.runAfterDelay(160, () -> {
            helper.assertTrue(controller.beeCount() == 1, "Idle courier did not enter controller");

            helper.assertTrue(controller.getBees().size() == 1, "Expected one stored occupant");

            helper.assertTrue(bee.isRemoved(), "Stored courier still exists in world");

            helper.assertTrue(controller.isCourierRegistered(id), "Courier lost colony registration");

            helper.succeed();
        });
    }

    public static void returnGracePeriod(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        ResourcefulBee bee = courier(helper, controller);

        bee.delayCourierReturn(100);

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(!bee.isRemoved(), "Courier returned during grace period");

            helper.assertTrue(controller.beeCount() == 0, "Controller stored courier too early");
        });

        helper.startSequence().thenWaitUntil(() -> {
            helper.assertTrue(controller.beeCount() > 0, "Courier has not entered the controller");
        }).thenSucceed();
    }

    public static void blockedEntrance(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        helper.setBlock(CONTROLLER.relative(Direction.EAST), Blocks.STONE);

        ResourcefulBee bee = courier(helper, controller);

        helper.runAfterDelay(100, () -> {
            helper.assertTrue(controller.beeCount() == 0, "Courier entered through blocked entrance");

            helper.assertTrue(!bee.isRemoved(), "Courier disappeared at blocked entrance");

            helper.setBlock(CONTROLLER.relative(Direction.EAST), Blocks.AIR);
        });

        helper.startSequence().thenWaitUntil(() -> {
            helper.assertTrue(controller.beeCount() > 0, "Courier failed to retry after entrance cleared" + " (beeRemoved=" + bee.isRemoved() + ", pollinating=" + bee.isPollinating() + ", canReturn=" + bee.canBeginCourierReturn() + ", assigned=" + bee.getCourierDelivery().isAssigned() + ", cargo=" + bee.hasCourierCargo() + ", position=" + bee.blockPosition() + ")");
        }).thenSucceed();
    }

    public static void unregisteredCourierRejected(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        controller.tryEnterHive(bee);

        helper.assertTrue(controller.beeCount() == 0, "Unregistered courier was stored");

        helper.assertTrue(!bee.isRemoved(), "Unregistered courier was discarded");

        helper.succeed();
    }

//    public static void cargoPreventsStorage(
//            GameTestHelper helper
//    ) {
//        ColonyControllerBlockEntity controller =
//                controller(helper);
//
//        ResourcefulBee bee = courier(helper, controller);
//
//        // Use a real cargo insertion transaction here,
//        // matching the existing courier transfer GameTests.
//        // This test must not be considered complete until
//        // hasCourierCargo() is confirmed true.
//
//        helper.assertTrue(
//                !bee.hasCourierCargo()
//                        || !controller.canAcceptReturningCourier(bee),
//                "Controller accepted courier with cargo"
//        );
//
//        helper.succeed();
//    }
}
