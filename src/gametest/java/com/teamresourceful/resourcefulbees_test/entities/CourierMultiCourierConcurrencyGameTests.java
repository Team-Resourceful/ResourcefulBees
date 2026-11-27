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
 * Phase 3B.5B.7E: concurrent couriers sharing source and destination inventories.
 */
public final class CourierMultiCourierConcurrencyGameTests {
    private static final BlockPos CONTROLLER = new BlockPos(2, 2, 2);
    private static final BlockPos SOURCE = new BlockPos(4, 2, 2);
    private static final BlockPos DEST = new BlockPos(12, 2, 2);
    private static final BlockPos START_A = new BlockPos(7, 3, 5);
    private static final BlockPos START_B = new BlockPos(8, 3, 7);
    private static final ItemResource IRON = ItemResource.of(new ItemStack(Items.IRON_INGOT));

    private CourierMultiCourierConcurrencyGameTests() {
    }

    private record Fixture(ColonyControllerBlockEntity controller, ChestBlockEntity source,
                           ChestBlockEntity destination, ResourcefulBee a, ResourcefulBee b) {
    }

    private static Fixture setup(GameTestHelper helper, int sourceIron, int cargoEach) {
        helper.setBlock(CONTROLLER, ModBlocks.COLONY_CONTROLLER.get());
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DEST, Blocks.CHEST);
        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);
        ChestBlockEntity destination = helper.getBlockEntity(DEST, ChestBlockEntity.class);
        source.clearContent();
        destination.clearContent();
        if (sourceIron > 0) source.setItem(0, new ItemStack(Items.IRON_INGOT, sourceIron));

        ColonyControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER, ColonyControllerBlockEntity.class);
        CourierRoute route = new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DEST), Direction.UP), true, 10, CourierRouteExecutionMode.COURIER, CourierFilter.NONE);
        helper.assertTrue(controller.addRoute(route), "Could not add shared-inventory route");

        ResourcefulBee a = createCourier(helper, controller, route, START_A, cargoEach);
        ResourcefulBee b = createCourier(helper, controller, route, START_B, cargoEach);
        helper.assertTrue(!a.getUUID().equals(b.getUUID()), "Courier UUIDs are not distinct");
        a.setNoAi(false);
        b.setNoAi(false);
        return new Fixture(controller, source, destination, a, b);
    }

    private static ResourcefulBee createCourier(GameTestHelper helper, ColonyControllerBlockEntity controller, CourierRoute route, BlockPos start, int cargo) {
        ResourcefulBee bee = TestUtilities.createSpawnedTestBee(helper);
        BlockPos pos = helper.absolutePos(start);
        bee.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        bee.setNoAi(true);
        helper.assertTrue(controller.registerCourier(bee), "Could not register courier");
        helper.assertTrue(bee.assignCourierRoute(new CourierAssignment(controller.getColonyId(), route.id()), helper.absolutePos(CONTROLLER)), "Could not assign courier");
        if (cargo > 0) {
            ((ItemStacksResourceHandler) bee.getCourierCargo()).set(0, IRON, cargo);
            helper.assertTrue(bee.getCourierDelivery().resumeDeliveryWithCargo(), "Could not start courier with preloaded cargo");
        }
        return bee;
    }

    private static int iron(ChestBlockEntity chest) {
        int count = 0;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (stack.is(Items.IRON_INGOT)) count += stack.getCount();
        }
        return count;
    }

    private static int cargo(GameTestHelper helper, ResourcefulBee bee) {
        int amount = bee.getCourierCargo().getAmountAsInt(0);
        if (amount > 0)
            helper.assertTrue(IRON.equals(bee.getCourierCargo().getResource(0)), "Courier cargo changed item type");
        return amount;
    }

    private static void assertConserved(GameTestHelper helper, Fixture f, int initial) {
        helper.assertTrue(f.a().isAlive() && f.b().isAlive(), "A courier unexpectedly died");
        helper.assertTrue(f.controller().isCourierRegistered(f.a().getUUID()) && f.controller().isCourierRegistered(f.b().getUUID()), "A courier lost its registration");
        int total = iron(f.source()) + cargo(helper, f.a()) + cargo(helper, f.b()) + iron(f.destination());
        helper.assertTrue(total == initial, "Iron not conserved: expected=" + initial + ", actual=" + total + ", source=" + iron(f.source()) + ", A=" + cargo(helper, f.a()) + ", B=" + cargo(helper, f.b()) + ", destination=" + iron(f.destination()));
    }

    private static void fillDestination(ChestBlockEntity destination) {
        for (int i = 0; i < destination.getContainerSize(); i++) {
            destination.setItem(i, new ItemStack(Items.COBBLESTONE, 64));
        }
    }

    /**
     * Both couriers compete to extract the same 12 iron ingots.
     */
    public static void sharedSourceContention(GameTestHelper helper) {
        Fixture f = setup(helper, 12, 0);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            assertConserved(helper, f, 12);
            if (iron(f.destination()) == 12) {
                helper.assertTrue(iron(f.source()) == 0 && cargo(helper, f.a()) == 0 && cargo(helper, f.b()) == 0, "Items remain after shared-source delivery");
                helper.succeed();
            } else if (ticks[0] >= 1500) {
                helper.fail("Shared-source delivery timed out: delivered=" + iron(f.destination()));
            }
        });
    }

    /**
     * Two loaded couriers insert into the same empty destination.
     */
    public static void sharedDestinationContention(GameTestHelper helper) {
        Fixture f = setup(helper, 0, 8);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            assertConserved(helper, f, 16);
            if (iron(f.destination()) == 16) {
                helper.assertTrue(cargo(helper, f.a()) == 0 && cargo(helper, f.b()) == 0, "Destination filled without both couriers unloading");
                helper.succeed();
            } else if (ticks[0] >= 1200) {
                helper.fail("Shared-destination delivery timed out: delivered=" + iron(f.destination()));
            }
        });
    }

    /**
     * Only four spaces are available across both couriers' 16 carried ingots.
     */
    public static void concurrentPartialInsertion(GameTestHelper helper) {
        Fixture f = setup(helper, 0, 8);
        fillDestination(f.destination());
        f.destination().setItem(0, new ItemStack(Items.IRON_INGOT, 60));
        final int[] ticks = {0};
        final boolean[] observedPartial = {false};
        helper.onEachTick(() -> {
            ticks[0]++;
            int inserted = iron(f.destination()) - 60;
            helper.assertTrue(inserted >= 0 && inserted <= 4, "Destination accepted more than its four available spaces");
            helper.assertTrue(inserted + cargo(helper, f.a()) + cargo(helper, f.b()) == 16, "Concurrent partial insertion lost or duplicated iron");
            if (inserted == 4) observedPartial[0] = true;
            if (ticks[0] >= 500) {
                helper.assertTrue(observedPartial[0], "No courier inserted into partial destination");
                helper.assertTrue(inserted == 4 && cargo(helper, f.a()) + cargo(helper, f.b()) == 12, "Couriers did not retain the twelve undelivered ingots");
                helper.succeed();
            }
        });
    }

    /**
     * Removing one loaded courier must not alter the other's eight ingots.
     */
    public static void courierRemovalDuringConcurrentDelivery(GameTestHelper helper) {
        Fixture f = setup(helper, 0, 8);
        // Prevent insertion until after removal; explicitly account for the removed bee's cargo.
        fillDestination(f.destination());
        f.a().setNoAi(true);
        f.b().setNoAi(true);
        int removedCargo = cargo(helper, f.a());
        helper.assertTrue(removedCargo == 8, "Unexpected cargo before courier removal");
        f.a().discard();
        helper.assertTrue(!f.a().isAlive(), "Removed courier remains alive");
        helper.assertTrue(f.b().isAlive() && cargo(helper, f.b()) == 8, "Removing courier A corrupted courier B");
        f.destination().clearContent();
        f.b().setNoAi(false);
        final int[] ticks = {0};
        helper.onEachTick(() -> {
            ticks[0]++;
            int total = removedCargo + cargo(helper, f.b()) + iron(f.destination());
            helper.assertTrue(total == 16, "Surviving courier's iron changed after removal");
            helper.assertTrue(f.b().isAlive(), "Surviving courier died");
            if (iron(f.destination()) == 8 && cargo(helper, f.b()) == 0) {
                helper.succeed();
            } else if (ticks[0] >= 1200) {
                helper.fail("Surviving courier did not deliver after peer removal");
            }
        });
    }

    /**
     * Sustained concurrent activity must conserve every item across all four locations.
     */
    public static void multiCourierConservation(GameTestHelper helper) {
        Fixture f = setup(helper, 16, 0);
        final int[] ticks = {0};
        final boolean[] observedCargo = {false};
        helper.onEachTick(() -> {
            ticks[0]++;
            assertConserved(helper, f, 16);
            if (cargo(helper, f.a()) + cargo(helper, f.b()) > 0) observedCargo[0] = true;
            if (iron(f.destination()) == 16) {
                helper.assertTrue(observedCargo[0], "Never observed iron in transit");
                helper.succeed();
            } else if (ticks[0] >= 1500) {
                helper.fail("Multi-courier conservation delivery timed out: delivered=" + iron(f.destination()));
            }
        });
    }
}
