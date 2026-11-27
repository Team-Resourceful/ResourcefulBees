package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.Map;
import java.util.UUID;

public final class CourierTransferGameTests {

    private static final BlockPos SOURCE = new BlockPos(1, 2, 1);
    private static final BlockPos DESTINATION = new BlockPos(3, 2, 1);

    private CourierTransferGameTests() {
    }

    private static CourierRoute setupRoute(GameTestHelper helper, boolean enabled) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), enabled, 0);
    }

    private static ChestBlockEntity chest(GameTestHelper helper, BlockPos pos) {
        return helper.getBlockEntity(pos, ChestBlockEntity.class);
    }

    private static int countItems(ChestBlockEntity chest) {
        int total = 0;

        for (int i = 0; i < chest.getContainerSize(); i++) {
            total += chest.getItem(i).getCount();
        }

        return total;
    }

    public static void transfersItems(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.itemsMoved() != 16) {
            helper.fail("Expected 16 transferred items, got " + result.itemsMoved());
            return;
        }

        if (countItems(source) != 16 || countItems(destination) != 16) {
            helper.fail("Incorrect inventory contents after transfer");
            return;
        }

        helper.succeed();
    }

    public static void emptySource(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.status() != TransferStatus.SOURCE_EMPTY) {
            helper.fail("Expected SOURCE_EMPTY, got " + result.status());
            return;
        }

        helper.succeed();
    }

    public static void disabledRoute(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, false);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.status() != TransferStatus.DISABLED || result.itemsMoved() != 0) {
            helper.fail("Disabled route performed a transfer");
            return;
        }

        if (countItems(source) != 32 || countItems(destination) != 0) {
            helper.fail("Disabled route modified inventories");
            return;
        }

        helper.succeed();
    }


    public static void partialTransfer(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        // Fill every destination slot. Leave room for 5 items
        // in the first slot.
        for (int i = 0; i < destination.getContainerSize(); i++) {
            destination.setItem(i, new ItemStack(Items.IRON_INGOT, 64));
        }
        destination.setItem(0, new ItemStack(Items.IRON_INGOT, 59));

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.status() != TransferStatus.PARTIAL || result.itemsMoved() != 5) {
            helper.fail("Expected PARTIAL transfer of 5, got " + result.status() + " / " + result.itemsMoved());
            return;
        }

        if (source.getItem(0).getCount() != 27 || destination.getItem(0).getCount() != 64) {
            helper.fail("Partial transfer produced incorrect inventory counts");
            return;
        }

        helper.succeed();
    }

    public static void fullDestination(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        for (int i = 0; i < destination.getContainerSize(); i++) {
            destination.setItem(i, new ItemStack(Items.IRON_INGOT, 64));
        }

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.status() != TransferStatus.DESTINATION_FULL || result.itemsMoved() != 0) {
            helper.fail("Expected DESTINATION_FULL with zero items moved");
            return;
        }

        if (countItems(source) != 32 || countItems(destination) != 27 * 64) {
            helper.fail("Full destination modified inventory contents");
            return;
        }

        helper.succeed();
    }

    public static void respectsTransferLimit(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 4);

        if (result.itemsMoved() != 4 || result.status() != TransferStatus.SUCCESS) {
            helper.fail("Expected exactly 4 transferred items");
            return;
        }

        if (countItems(source) != 28 || countItems(destination) != 4) {
            helper.fail("Transfer limit test produced incorrect counts");
            return;
        }

        helper.succeed();
    }

    public static void preservesItemCount(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 64));
        source.setItem(1, new ItemStack(Items.IRON_INGOT, 32));

        int originalTotal = countItems(source) + countItems(destination);
        int totalMoved = 0;

        for (int attempt = 0; attempt < 8; attempt++) {
            var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

            totalMoved += result.itemsMoved();

            int currentTotal = countItems(source) + countItems(destination);

            if (currentTotal != originalTotal) {
                helper.fail("Items were created or destroyed on attempt " + attempt);
                return;
            }
        }

        if (totalMoved != 96 || countItems(source) != 0 || countItems(destination) != 96) {
            helper.fail("Repeated transfers produced incorrect totals");
            return;
        }

        helper.succeed();
    }

    public static void differentItemTypes(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));
        destination.setItem(0, new ItemStack(Items.GOLD_INGOT, 32));

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.itemsMoved() != 16) {
            helper.fail("Expected 16 iron ingots transferred");
            return;
        }

        if (!destination.getItem(0).is(Items.GOLD_INGOT) || destination.getItem(0).getCount() != 32) {
            helper.fail("Existing gold stack was modified");
            return;
        }

        int ironCount = 0;
        for (int i = 0; i < destination.getContainerSize(); i++) {
            ItemStack stack = destination.getItem(i);
            if (stack.is(Items.IRON_INGOT)) {
                ironCount += stack.getCount();
            }
        }

        if (ironCount != 16 || countItems(source) != 16) {
            helper.fail("Item types were merged or transferred incorrectly");
            return;
        }

        helper.succeed();
    }

    public static void missingDestination(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        source.setItem(0, new ItemStack(Items.IRON_INGOT, 32));

        // Remove the destination inventory after creating the route.
        helper.setBlock(DESTINATION, Blocks.AIR);

        var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

        if (result.status() != TransferStatus.DESTINATION_UNAVAILABLE || result.itemsMoved() != 0) {
            helper.fail("Expected DESTINATION_UNAVAILABLE");
            return;
        }

        if (countItems(source) != 32) {
            helper.fail("Missing destination caused item loss");
            return;
        }

        helper.succeed();
    }

    public static void unsupportedResource(GameTestHelper helper) {
        setupRoute(helper, true);

        for (CourierResourceType type : new CourierResourceType[]{CourierResourceType.FLUID, CourierResourceType.ENERGY}) {
            CourierRoute route = new CourierRoute(UUID.randomUUID(), type, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), true, 0);

            var result = CourierTransferEngine.transfer(helper.getLevel(), route, 16);

            if (result.status() != TransferStatus.UNSUPPORTED_RESOURCE || result.itemsMoved() != 0) {
                helper.fail("Expected unsupported resource rejection for " + type);
                return;
            }
        }

        helper.succeed();
    }

    public static void schedulerRespectsInterval(GameTestHelper helper) {
        CourierRoute route = setupRoute(helper, true);

        ChestBlockEntity source = chest(helper, SOURCE);
        ChestBlockEntity destination = chest(helper, DESTINATION);

        source.setItem(0, new ItemStack(Items.IRON_INGOT, 64));

        CourierRouteScheduler scheduler = new CourierRouteScheduler();
        scheduler.setInterval(20);
        scheduler.setTransferLimit(4);

        Map<UUID, CourierRoute> routes = Map.of(route.id(), route);

        scheduler.tick(helper.getLevel(), routes, 100);

        if (countItems(destination) != 4) {
            helper.fail("First scheduler execution did not transfer 4 items");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 110);

        if (countItems(destination) != 4) {
            helper.fail("Scheduler executed before interval elapsed");
            return;
        }

        scheduler.tick(helper.getLevel(), routes, 120);

        if (countItems(destination) != 8) {
            helper.fail("Scheduler did not execute at the next interval");
            return;
        }

        helper.succeed();
    }

}
