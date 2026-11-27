package com.teamresourceful.resourcefulbees_test.entities;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees_test.util.TestUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.UUID;

/**
 * Phase 3B.5B.7D: lossless delivery under unavailable or capacity-limited destinations.
 */
public final class CourierDeliveryFailureGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DEST = new BlockPos(12, 2, 2);
    private static final BlockPos START = new BlockPos(8, 3, 6);
    private static final ItemResource IRON = ItemResource.of(new ItemStack(Items.IRON_INGOT));
    private static final int INITIAL_CARGO = 8;

    private CourierDeliveryFailureGameTests() {
    }

    private record Fixture(ColonyControllerBlockEntity controller, ResourcefulBee bee, ChestBlockEntity destination,
                           UUID routeId) {
    }

    private static Fixture setup(GameTestHelper helper) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DEST, Blocks.CHEST);
        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);
        ChestBlockEntity destination = helper.getBlockEntity(DEST, ChestBlockEntity.class);
        source.clearContent();
        destination.clearContent();
        ColonyControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        UUID routeId = UUID.randomUUID();
        CourierRoute route = new CourierRoute(routeId, CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DEST), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
        helper.assertTrue(controller.addRoute(route), "Could not add delivery failure route");
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        BlockPos start = helper.absolutePos(START);
        bee.setPos(start.getX() + 0.5D, start.getY() + 0.5D, start.getZ() + 0.5D);
        bee.setNoAi(true);
        helper.assertTrue(controller.registerCourier(bee), "Could not register courier");
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), routeId), helper.absolutePos(CONTROLLER)), "Could not assign courier");
        ((ItemStacksResourceHandler) bee.getCourierCargo()).set(0, IRON, INITIAL_CARGO);
        helper.assertTrue(bee.getCourierDelivery().resumeDeliveryWithCargo(), "Could not start delivery with existing cargo");
        return new Fixture(controller, bee, destination, routeId);
    }

    private static int cargo(GameTestHelper helper, ResourcefulBee bee) {
        int amount = bee.getCourierCargo().getAmountAsInt(0);
        if (amount > 0) {
            helper.assertTrue(IRON.equals(bee.getCourierCargo().getResource(0)), "Courier cargo item changed");
        }
        return amount;
    }

    private static int iron(ChestBlockEntity chest) {
        int count = 0;
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            ItemStack stack = chest.getItem(slot);
            if (stack.is(Items.IRON_INGOT)) count += stack.getCount();
        }
        return count;
    }

    private static void fillWithCobblestone(ChestBlockEntity chest) {
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            chest.setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
    }

    private static void assertConserved(GameTestHelper helper, Fixture f, int delivered) {
        helper.assertTrue(f.bee().isAlive(), "Courier died during delivery failure");
        helper.assertTrue(cargo(helper, f.bee()) + delivered == INITIAL_CARGO, "Iron cargo was lost or duplicated");
        helper.assertTrue(f.controller().isCourierRegistered(f.bee().getUUID()), "Courier registration unexpectedly changed");
    }

    /**
     * A full destination must not delete or eject carried iron.
     */
    public static void destinationFullRetainsCargo(GameTestHelper helper) {
        Fixture f = setup(helper);
        fillWithCobblestone(f.destination());
        f.bee().setNoAi(false);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            assertConserved(helper, f, iron(f.destination()));
            helper.assertTrue(cargo(helper, f.bee()) == INITIAL_CARGO, "Full destination consumed courier cargo");
            if (ticks[0] >= 300) helper.succeed();
        });
    }

    /**
     * Destroying the destination must not destroy items already in flight.
     */
    public static void destinationRemovedPreservesCargo(GameTestHelper helper) {
        Fixture f = setup(helper);
        helper.setBlock(DEST, Blocks.AIR);
        f.bee().setNoAi(false);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            helper.assertTrue(helper.getBlockState(DEST).isAir(), "Destination unexpectedly reappeared");
            assertConserved(helper, f, 0);
            if (ticks[0] >= 300) helper.succeed();
        });
    }

    /**
     * Replacing the missing endpoint should allow the same cargo to arrive.
     */
    public static void destinationRestoredResumesDelivery(GameTestHelper helper) {
        Fixture f = setup(helper);
        helper.setBlock(DEST, Blocks.AIR);
        f.bee().setNoAi(false);
        final int[] ticks = {0};
        final boolean[] restored = {false};
        helper.onEachTick(() -> {
            ticks[0]++;
            if (!restored[0] && ticks[0] >= 120) {
                helper.assertTrue(cargo(helper, f.bee()) == INITIAL_CARGO, "Cargo changed before endpoint restoration");
                helper.setBlock(DEST, Blocks.CHEST);
                helper.getBlockEntity(DEST, ChestBlockEntity.class).clearContent();
                restored[0] = true;
            }
            int delivered = restored[0] ? iron(helper.getBlockEntity(DEST, ChestBlockEntity.class)) : 0;
            assertConserved(helper, f, delivered);
            if (restored[0] && delivered == INITIAL_CARGO && cargo(helper, f.bee()) == 0) {
                helper.succeed();
            } else if (ticks[0] >= 1200) {
                helper.fail("Courier did not resume delivery after destination restoration: state=" + f.bee().getCourierState() + ", cargo=" + cargo(helper, f.bee()));
            }
        });
    }

    /**
     * Only four ingots fit: the other four must remain with the courier.
     */
    public static void partialInsertionRetainsRemainder(GameTestHelper helper) {
        Fixture f = setup(helper);
        fillWithCobblestone(f.destination());
        f.destination().setItem(0, new ItemStack(Items.IRON_INGOT, 60));
        f.bee().setNoAi(false);
        final int[] ticks = {0};
        final boolean[] observedPartial = {false};
        helper.onEachTick(() -> {
            ticks[0]++;
            int inserted = iron(f.destination()) - 60;
            helper.assertTrue(inserted >= 0 && inserted <= 4, "Destination inserted beyond available capacity");
            assertConserved(helper, f, inserted);
            if (inserted == 4 && cargo(helper, f.bee()) == 4) observedPartial[0] = true;
            if (ticks[0] >= 450) {
                helper.assertTrue(observedPartial[0], "Courier never performed the expected partial insertion");
                helper.assertTrue(inserted == 4 && cargo(helper, f.bee()) == 4, "Remaining cargo changed after partial insertion");
                helper.succeed();
            }
        });
    }

    /**
     * Sustained failure must not consume, duplicate, or drop the retained cargo.
     */
    public static void repeatedDeliveryFailuresRemainLossless(GameTestHelper helper) {
        Fixture f = setup(helper);
        fillWithCobblestone(f.destination());
        f.bee().setNoAi(false);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            assertConserved(helper, f, iron(f.destination()));
            helper.assertTrue(cargo(helper, f.bee()) == INITIAL_CARGO, "Repeated insertion failure altered retained cargo");
            helper.assertTrue(f.destination().getItem(0).is(Items.COBBLESTONE), "Destination contents unexpectedly changed");
            if (ticks[0] >= 900) helper.succeed();
        });
    }
}
