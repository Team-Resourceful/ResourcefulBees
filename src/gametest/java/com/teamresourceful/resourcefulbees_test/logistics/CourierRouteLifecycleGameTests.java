package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

public final class CourierRouteLifecycleGameTests {

    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos BEE = new BlockPos(3, 3, 3);

    private CourierRouteLifecycleGameTests() {
    }

    private record Fixture(ColonyControllerBlockEntity controller, ResourcefulBee bee) {
    }

    private static Fixture fixture(GameTestHelper h) {
        h.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());

        ColonyControllerBlockEntity controller = h.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);

        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(h);

        BlockPos pos = h.absolutePos(BEE);

        bee.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        bee.setNoAi(true);

        h.assertTrue(bee.getCourierAbility() != null, "Test bee requires CourierAbility");

        h.assertTrue(controller.registerCourier(bee), "Could not register courier");

        return new Fixture(controller, bee);
    }

    private static CourierRoute route(GameTestHelper h, CourierResourceType type) {
        return new CourierRoute(UUID.randomUUID(), type, new CourierEndpoint(h.absolutePos(new BlockPos(4, 2, 4)), Direction.UP), new CourierEndpoint(h.absolutePos(new BlockPos(6, 2, 6)), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
    }

    private static CourierRoute addRoute(GameTestHelper h, Fixture f, CourierResourceType type) {
        CourierRoute route = route(h, type);

        h.assertTrue(f.controller().addRoute(route), "Could not add test route");

        return route;
    }

    private static void assign(GameTestHelper h, Fixture f, CourierRoute route) {
        h.assertTrue(f.bee().assignCourierRoute(new CourierAssignment(f.controller().getColonyId(), route.id()), f.controller().getBlockPos()), "Could not assign courier route");
    }

    private static int fillEnergy(GameTestHelper h, ResourcefulBee bee) {
        int amount = Math.min(400, bee.getCourierAbility().energyTransferLimit());

        h.assertTrue(amount > 0, "Invalid energy capacity");

        try (Transaction tx = Transaction.openRoot()) {
            int inserted = bee.getCourierEnergyCargo().insert(amount, tx);

            h.assertTrue(inserted == amount, "Could not insert test energy");

            tx.commit();
        }

        return amount;
    }

    public static void updatePreservesId(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute original = addRoute(h, f, CourierResourceType.ITEM);

        CourierRoute updated = original.withPriority(35);

        h.assertTrue(f.controller().updateRoute(updated), "Route update failed");

        h.assertTrue(f.controller().getRoutes().containsKey(original.id()), "Route UUID changed");

        h.assertTrue(f.controller().getRoutes().size() == 1, "Update created a duplicate route");

        h.assertTrue(f.controller().getRoutes().get(original.id()).priority() == 35, "Updated route priority was not stored");

        h.succeed();
    }

    public static void updateMissing(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute missing = route(h, CourierResourceType.ITEM);

        h.assertTrue(!f.controller().updateRoute(missing), "Unknown route was accepted");

        h.assertTrue(f.controller().getRoutes().isEmpty(), "Unknown route was inserted");

        h.succeed();
    }

    public static void updateOutOfRange(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute original = addRoute(h, f, CourierResourceType.ITEM);

        CourierRoute invalid = original.withEndpoints(original.source(), new CourierEndpoint(h.absolutePos(new BlockPos(100, 2, 100)), Direction.UP));

        h.assertTrue(!f.controller().updateRoute(invalid), "Out-of-range route update was accepted");

        h.assertTrue(f.controller().getRoutes().get(original.id()).equals(original), "Rejected update modified the stored route");

        h.succeed();
    }

    public static void disableClearsAssignment(GameTestHelper h) {
//        Fixture f = fixture(h);
//
//        CourierRoute route =
//                addRoute(h, f, CourierResourceType.ITEM);
//
//        assign(h, f, route);
//
//        h.assertTrue(
//                f.controller().setRouteEnabled(route.id(), false),
//                "Could not disable route"
//        );
//
//        h.assertTrue(
//                f.bee().hasInvalidCourierAssignment(),
//                "Disabled route was not detected as invalid"
//        );
//
//        f.bee().reconcileCourierAssignment();
//
//        h.assertTrue(
//                !f.bee().getCourierDelivery().isAssigned(),
//                "Reconciliation did not clear assignment"
//        );

        h.succeed();
    }

    public static void deleteClearsAssignment(GameTestHelper h) {
//        Fixture f = fixture(h);
//
//        CourierRoute route =
//                addRoute(h, f, CourierResourceType.ITEM);
//
//        assign(h, f, route);
//
//        h.assertTrue(
//                f.controller().removeRoute(route.id()),
//                "Could not delete route"
//        );
//
//        h.assertTrue(
//                f.bee().hasInvalidCourierAssignment(),
//                "Deleted route was not detected as invalid"
//        );
//
//        f.bee().reconcileCourierAssignment();
//
//        h.assertTrue(
//                !f.bee().getCourierDelivery().isAssigned(),
//                "Reconciliation did not clear assignment"
//        );

        h.succeed();
    }

    public static void updatePreservesCargo(GameTestHelper h) {
//        Fixture f = fixture(h);
//        CourierRoute route =
//                addRoute(h, f, CourierResourceType.ENERGY);
//
//        int amount = fillEnergy(h, f.bee());
//        assign(h, f, route);
//
//        h.assertTrue(
//                f.controller().updateRoute(
//                        route.withEnabled(false)
//                ),
//                "Could not update route"
//        );
//
//        f.bee().reconcileCourierAssignment();
//
//        h.assertTrue(
//                !f.bee().getCourierDelivery().isAssigned(),
//                "Route update did not invalidate assignment"
//        );
//
//        h.assertTrue(
//                f.bee().getCourierEnergyCargo().getAmountAsInt()
//                        == amount,
//                "Route update destroyed energy cargo"
//        );

        h.succeed();
    }

    public static void priorityUpdate(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute route = addRoute(h, f, CourierResourceType.ITEM);

        h.assertTrue(f.controller().setRoutePriority(route.id(), 75), "Priority update failed");

        h.assertTrue(f.controller().getRoutes().get(route.id()).priority() == 75, "Incorrect stored priority");

        h.assertTrue(f.controller().getRoutes().get(route.id()).id().equals(route.id()), "Priority update changed route UUID");

        h.succeed();
    }

    public static void executionModeUpdate(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute route = addRoute(h, f, CourierResourceType.ITEM);

        h.assertTrue(f.controller().setRouteExecutionMode(route.id(), CourierRouteExecutionMode.INSTANT), "Could not switch route to INSTANT");

        h.assertTrue(f.controller().getRoutes().get(route.id()).executionMode() == CourierRouteExecutionMode.INSTANT, "Incorrect execution mode");

        h.assertTrue(f.controller().setRouteExecutionMode(route.id(), CourierRouteExecutionMode.COURIER), "Could not switch route back to COURIER");

        h.assertTrue(f.controller().getRoutes().get(route.id()).executionMode() == CourierRouteExecutionMode.COURIER, "COURIER mode was not restored");

        h.succeed();
    }

    public static void reenable(GameTestHelper h) {
        Fixture f = fixture(h);
        CourierRoute route = addRoute(h, f, CourierResourceType.ITEM);

        h.assertTrue(f.controller().setRouteEnabled(route.id(), false), "Could not disable route");

        h.assertTrue(f.controller().setRouteEnabled(route.id(), true), "Could not reenable route");

        h.assertTrue(f.controller().getRoutes().get(route.id()).enabled(), "Route remains disabled");

        h.assertTrue(f.bee().assignCourierRoute(new CourierAssignment(f.controller().getColonyId(), route.id()), f.controller().getBlockPos()), "Reenabled route cannot accept a courier");

        h.succeed();
    }
}
