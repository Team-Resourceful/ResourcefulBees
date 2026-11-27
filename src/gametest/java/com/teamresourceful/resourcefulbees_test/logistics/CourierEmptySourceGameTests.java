package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteExecutionMode;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.UUID;

public final class CourierEmptySourceGameTests {

    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos EMPTY_SOURCE = new BlockPos(6, 2, 2);
    private static final BlockPos EMPTY_DEST = new BlockPos(8, 2, 2);
    private static final BlockPos WORK_SOURCE = new BlockPos(6, 2, 5);
    private static final BlockPos WORK_DEST = new BlockPos(8, 2, 5);

    private CourierEmptySourceGameTests() {
    }

    private static ColonyControllerBlockEntity controller(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        helper.setBlock(CONTROLLER, helper.getBlockState(CONTROLLER).setValue(BeehiveBlock.FACING, Direction.EAST));
        return helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
    }

    private static CourierRoute route(GameTestHelper helper, ColonyControllerBlockEntity controller, BlockPos source, BlockPos destination) {
        helper.setBlock(source, Blocks.CHEST);
        helper.setBlock(destination, Blocks.CHEST);

        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(source), Direction.UP), new CourierEndpoint(helper.absolutePos(destination), Direction.UP), true, 0).withExecutionMode(CourierRouteExecutionMode.COURIER);

        helper.assertTrue(controller.addRoute(route), "Failed to register courier route");
        return route;
    }

    private static ResourcefulBee courier(GameTestHelper helper, ColonyControllerBlockEntity controller) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);

        BlockPos source = helper.absolutePos(EMPTY_SOURCE);
        bee.setPos(source.getX() + 0.5D, source.getY() + 0.5D, source.getZ() + 0.5D);
        bee.setNoAi(false);

        helper.assertTrue(controller.registerCourier(bee), "Failed to register courier");
        return bee;
    }

    private static void assign(GameTestHelper helper, ColonyControllerBlockEntity controller, ResourcefulBee bee, CourierRoute route) {
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), route.id()), controller.getBlockPos()), "Failed to assign initial route");
    }

    private static void fillSource(GameTestHelper helper, BlockPos position) {
        ChestBlockEntity chest = helper.getBlockEntity(position, ChestBlockEntity.class);
        chest.setItem(0, new ItemStack(Items.IRON_INGOT, 16));
        chest.setChanged();
    }

    // Test 1: No resources available at the source.
    public static void emptySourceClearsAssignment(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        CourierRoute emptyRoute = route(helper, controller, EMPTY_SOURCE, EMPTY_DEST);

        ResourcefulBee bee = courier(helper, controller);
        assign(helper, controller, bee, emptyRoute);

        helper.assertTrue(bee.getCourierDelivery().isAssigned(), "Courier was never assigned");

        helper.runAfterDelay(60, () -> {
            helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Courier remained assigned to an empty source");
            helper.assertTrue(bee.getCourierState() == CourierState.IDLE, "Courier did not transition to IDLE");
            helper.assertTrue(!bee.hasCourierCargo(), "Empty-source courier unexpectedly acquired cargo");
            helper.succeed();
        });
    }

    // Test 2: Empty route abandoned; productive route selected.
    public static void emptySourceReassignsCourier(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        CourierRoute emptyRoute = route(helper, controller, EMPTY_SOURCE, EMPTY_DEST);
        CourierRoute workingRoute = route(helper, controller, WORK_SOURCE, WORK_DEST);

        fillSource(helper, WORK_SOURCE);

        ResourcefulBee bee = courier(helper, controller);
        assign(helper, controller, bee, emptyRoute);

        helper.startSequence().thenWaitUntil(() -> {
            var assignment = bee.getCourierDelivery().getAssignment();

            helper.assertTrue(assignment != null && assignment.routeId().equals(workingRoute.id()), "Courier has not been assigned to productive route");
        }).thenSucceed();
    }

    // Test 3: No productive route; courier returns to storage.
    public static void emptySourceReturnsToController(GameTestHelper helper) {
        ColonyControllerBlockEntity controller = controller(helper);

        CourierRoute emptyRoute = route(helper, controller, EMPTY_SOURCE, EMPTY_DEST);

        ResourcefulBee bee = courier(helper, controller);
        assign(helper, controller, bee, emptyRoute);

        helper.runAfterDelay(60, () -> {
            helper.assertTrue(!bee.getCourierDelivery().isAssigned(), "Courier did not abandon empty source");
            helper.assertTrue(!bee.hasCourierCargo(), "Courier unexpectedly contains cargo");
        });

        helper.startSequence().thenWaitUntil(() -> {
            helper.assertTrue(controller.beeCount() > 0, "Courier has not entered the controller");
        }).thenSucceed();
    }
}
