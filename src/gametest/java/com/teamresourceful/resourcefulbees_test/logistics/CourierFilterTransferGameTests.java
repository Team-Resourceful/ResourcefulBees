package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class CourierFilterTransferGameTests {

    private static final BlockPos SOURCE = new BlockPos(2, 2, 2);
    private static final BlockPos DESTINATION = new BlockPos(5, 2, 2);

    private CourierFilterTransferGameTests() {
    }

    private record Fixture(ServerLevel level, ChestBlockEntity source, ChestBlockEntity destination,
                           ResourceHandler<ItemResource> sourceHandler,
                           ResourceHandler<ItemResource> destinationHandler) {
    }

    private static Fixture fixture(GameTestHelper helper) {
        helper.setBlock(SOURCE, Blocks.CHEST);
        helper.setBlock(DESTINATION, Blocks.CHEST);

        ServerLevel level = helper.getLevel();

        ChestBlockEntity source = helper.getBlockEntity(SOURCE, ChestBlockEntity.class);
        ChestBlockEntity destination = helper.getBlockEntity(DESTINATION, ChestBlockEntity.class);

        source.clearContent();
        destination.clearContent();

        ResourceHandler<ItemResource> sourceHandler = level.getCapability(Capabilities.Item.BLOCK, helper.absolutePos(SOURCE), Direction.UP);

        ResourceHandler<ItemResource> destinationHandler = level.getCapability(Capabilities.Item.BLOCK, helper.absolutePos(DESTINATION), Direction.UP);

        helper.assertTrue(sourceHandler != null && destinationHandler != null, "Could not resolve chest item capabilities");

        return new Fixture(level, source, destination, sourceHandler, destinationHandler);
    }

    private static CourierFilter ironWhitelist() {
        return new CourierFilter(CourierFilterMode.WHITELIST, List.of(new CourierFilterEntry(net.minecraft.resources.Identifier.parse("minecraft:iron_ingot"))));
    }

    private static CourierFilter ironBlacklist() {
        return new CourierFilter(CourierFilterMode.BLACKLIST, List.of(new CourierFilterEntry(net.minecraft.resources.Identifier.parse("minecraft:iron_ingot"))));
    }

    private static CourierRoute route(GameTestHelper helper, CourierFilter filter) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(helper.absolutePos(SOURCE), Direction.UP), new CourierEndpoint(helper.absolutePos(DESTINATION), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, filter);
    }

    private static Predicate<ItemResource> allowed(CourierFilter filter) {
        return resource -> filter.allows(resource.toStack().getItem());
    }

    private static void assertStack(GameTestHelper helper, ItemStack stack, net.minecraft.world.item.Item expectedItem, int expectedCount, String message) {
        helper.assertTrue(stack.is(expectedItem) && stack.getCount() == expectedCount, message);
    }

    // 1. Mixed source inventory skips excluded items.
    public static void filteredTransferSkipsExcluded(GameTestHelper helper) {
        Fixture f = fixture(helper);

        f.source().setItem(0, new ItemStack(Items.GOLD_INGOT, 8));
        f.source().setItem(1, new ItemStack(Items.IRON_INGOT, 12));

        var result = CourierTransferEngine.transferItems(f.sourceHandler(), f.destinationHandler(), 16, allowed(ironWhitelist()));

        helper.assertTrue(result.itemsMoved() == 12, "Expected 12 eligible iron ingots to transfer");

        assertStack(helper, f.source().getItem(0), Items.GOLD_INGOT, 8, "Excluded gold was modified");

        helper.assertTrue(f.source().getItem(1).isEmpty(), "Transferred iron remained in source");

        assertStack(helper, f.destination().getItem(0), Items.IRON_INGOT, 12, "Destination did not receive iron");

        helper.succeed();
    }

    // 2. No matching items leaves inventories unchanged.
    public static void filteredTransferNoMatch(GameTestHelper helper) {
        Fixture f = fixture(helper);

        f.source().setItem(0, new ItemStack(Items.GOLD_INGOT, 8));

        var result = CourierTransferEngine.transferItems(f.sourceHandler(), f.destinationHandler(), 16, allowed(ironWhitelist()));

        helper.assertTrue(result.status() == TransferStatus.SOURCE_EMPTY && result.itemsMoved() == 0, "Expected SOURCE_EMPTY with zero transferred items");

        assertStack(helper, f.source().getItem(0), Items.GOLD_INGOT, 8, "Rejected source items were modified");

        helper.assertTrue(f.destination().isEmpty(), "Destination received rejected items");

        helper.succeed();
    }

    // 3. A destination with limited space accepts only what fits.
    public static void filteredTransferPartial(GameTestHelper helper) {
        Fixture f = fixture(helper);

        f.source().setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        // Fill every destination slot except one.
        for (int slot = 0; slot < f.destination().getContainerSize(); slot++) {
            f.destination().setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }

        f.destination().setItem(0, new ItemStack(Items.IRON_INGOT, 60));

        var result = CourierTransferEngine.transferItems(f.sourceHandler(), f.destinationHandler(), 16, allowed(ironWhitelist()));

        helper.assertTrue(result.status() == TransferStatus.PARTIAL && result.itemsMoved() == 4, "Expected a partial transfer of four items");

        assertStack(helper, f.source().getItem(0), Items.IRON_INGOT, 12, "Source did not retain the remaining 12 iron");

        assertStack(helper, f.destination().getItem(0), Items.IRON_INGOT, 64, "Destination iron stack was not filled");

        helper.succeed();
    }

    // 4. A rejected insertion must roll back extraction.
    public static void filteredTransferRollback(GameTestHelper helper) {
        Fixture f = fixture(helper);

        f.source().setItem(0, new ItemStack(Items.IRON_INGOT, 16));

        for (int slot = 0; slot < f.destination().getContainerSize(); slot++) {
            f.destination().setItem(slot, new ItemStack(Items.COBBLESTONE, 64));
        }

        var result = CourierTransferEngine.transferItems(f.sourceHandler(), f.destinationHandler(), 16, allowed(ironWhitelist()));

        helper.assertTrue(result.status() == TransferStatus.DESTINATION_FULL && result.itemsMoved() == 0, "Expected full destination to reject transfer");

        assertStack(helper, f.source().getItem(0), Items.IRON_INGOT, 16, "Failed transfer consumed source items");

        for (int slot = 0; slot < f.destination().getContainerSize(); slot++) {
            assertStack(helper, f.destination().getItem(slot), Items.COBBLESTONE, 64, "Failed transfer modified destination");
        }

        helper.succeed();
    }

    // 5. Courier pickup must respect the route whitelist.
    public static void filteredPickup(GameTestHelper helper) {
        Fixture f = fixture(helper);

        f.source().setItem(0, new ItemStack(Items.GOLD_INGOT, 8));
        f.source().setItem(1, new ItemStack(Items.IRON_INGOT, 12));

        ItemStacksResourceHandler cargo = new ItemStacksResourceHandler(1);

        CourierRoute route = route(helper, ironWhitelist());

        var result = CourierDeliveryExecutor.pickup(f.level(), route, cargo, 16);

        helper.assertTrue(result.itemsMoved() == 12, "Courier did not pick up 12 permitted iron ingots");

        helper.assertTrue(cargo.getAmountAsInt(0) == 12 && cargo.getResource(0).equals(ItemResource.of(new ItemStack(Items.IRON_INGOT))), "Courier cargo contains incorrect items");

        assertStack(helper, f.source().getItem(0), Items.GOLD_INGOT, 8, "Courier extracted excluded gold");

        helper.assertTrue(f.source().getItem(1).isEmpty(), "Courier failed to extract permitted iron");

        helper.succeed();
    }

    // 6. Existing cargo may be delivered despite a changed filter.
    public static void filteredDeliveryPreservesCargo(GameTestHelper helper) {
        Fixture f = fixture(helper);

        ItemStacksResourceHandler cargo = new ItemStacksResourceHandler(1);

        ItemResource iron = ItemResource.of(new ItemStack(Items.IRON_INGOT));

        cargo.set(0, iron, 8);

        // This filter explicitly rejects the item already in cargo.
        CourierRoute route = route(helper, ironBlacklist());

        var result = CourierDeliveryExecutor.deliver(f.level(), route, cargo, 16);

        helper.assertTrue(result.itemsMoved() == 8, "Existing cargo was incorrectly blocked by the filter");

        helper.assertTrue(cargo.getAmountAsInt(0) == 0, "Delivered cargo was not removed from courier");

        assertStack(helper, f.destination().getItem(0), Items.IRON_INGOT, 8, "Destination did not receive existing cargo");

        helper.succeed();
    }
}
