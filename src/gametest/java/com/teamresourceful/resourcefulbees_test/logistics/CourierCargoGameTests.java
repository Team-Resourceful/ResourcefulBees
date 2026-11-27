package com.teamresourceful.resourcefulbees_test.logistics;

import com.teamresourceful.resourcefulbees.common.logistics.CourierTransferEngine;
import com.teamresourceful.resourcefulbees.common.logistics.TransferStatus;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryController;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

public final class CourierCargoGameTests {

    private CourierCargoGameTests() {
    }

    private static ItemStacksResourceHandler cargo() {
        return new ItemStacksResourceHandler(1) {
            @Override
            protected int getCapacity(int index, ItemResource resource) {
                return Math.min(16, super.getCapacity(index, resource));
            }
        };
    }

    public static void courierCargoCapacity(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();

        int inserted;

        try (Transaction transaction = Transaction.openRoot()) {
            inserted = cargo.insert(ItemResource.of(Items.IRON_INGOT), 32, transaction);
            transaction.commit();
        }

        if (inserted != 16 || cargo.getAmountAsInt(0) != 16) {
            helper.fail("Cargo exceeded its 16-item capacity");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoRejectsMixedItems(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();

        try (Transaction transaction = Transaction.openRoot()) {
            cargo.insert(ItemResource.of(Items.IRON_INGOT), 8, transaction);
            transaction.commit();
        }

        int inserted;

        try (Transaction transaction = Transaction.openRoot()) {
            inserted = cargo.insert(ItemResource.of(Items.GOLD_INGOT), 8, transaction);
            transaction.commit();
        }

        if (inserted != 0 || cargo.getAmountAsInt(0) != 8 || !cargo.getResource(0).is(Items.IRON_INGOT)) {
            helper.fail("Cargo accepted incompatible items");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoRollback(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();

        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = cargo.insert(ItemResource.of(Items.IRON_INGOT), 12, transaction);

            if (inserted != 12) {
                helper.fail("Test setup failed to insert 12 items");
                return;
            }

            // Intentionally do not commit.
        }

        if (cargo.getAmountAsInt(0) != 0) {
            helper.fail("Uncommitted cargo insertion was not rolled back");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoConservation(GameTestHelper helper) {
        ItemStacksResourceHandler source = new ItemStacksResourceHandler(1);
        ItemStacksResourceHandler cargo = cargo();
        ItemStacksResourceHandler destination = new ItemStacksResourceHandler(1);

        source.set(0, ItemResource.of(Items.IRON_INGOT), 40);

        var pickup = CourierTransferEngine.transferItems(source, cargo, 16);

        if (pickup.itemsMoved() != 16) {
            helper.fail("Pickup did not move 16 items into cargo");
            return;
        }

        var delivery = CourierTransferEngine.transferItems(cargo, destination, 16);

        if (delivery.itemsMoved() != 16) {
            helper.fail("Delivery did not move 16 items out of cargo");
            return;
        }

        int total = source.getAmountAsInt(0) + cargo.getAmountAsInt(0) + destination.getAmountAsInt(0);

        if (total != 40 || source.getAmountAsInt(0) != 24 || cargo.getAmountAsInt(0) != 0 || destination.getAmountAsInt(0) != 16) {
            helper.fail("Item conservation failed during courier transfer");
            return;
        }

        helper.succeed();
    }


    public static void courierCargoPersistence(GameTestHelper helper) {
        ItemStacksResourceHandler original = cargo();

        ItemStack namedStack = new ItemStack(Items.DIAMOND, 11);
        namedStack.set(DataComponents.CUSTOM_NAME, Component.literal("Courier Cargo Test"));

        ItemResource resource = ItemResource.of(namedStack);

        try (Transaction tx = Transaction.openRoot()) {
            if (original.insert(resource, 11, tx) != 11) {
                helper.fail("Could not populate cargo");
                return;
            }
            tx.commit();
        }

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());

        output.putChild("CourierCargo", original);

        CompoundTag saved = output.buildResult();

        TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved);

        ItemStacksResourceHandler restored = cargo();
        input.readChild("CourierCargo", restored);

        if (restored.getAmountAsInt(0) != 11 || !restored.getResource(0).equals(resource)) {
            helper.fail("Cargo type, count, or components changed after reload");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoAssignmentClearing(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();
        CourierDeliveryController delivery = new CourierDeliveryController();

        CourierAssignment assignment = new CourierAssignment(UUID.randomUUID(), UUID.randomUUID());

        if (!delivery.assign(assignment)) {
            helper.fail("Could not assign courier");
            return;
        }

        try (Transaction tx = Transaction.openRoot()) {
            if (cargo.insert(ItemResource.of(Items.IRON_INGOT), 12, tx) != 12) {
                helper.fail("Could not populate cargo");
                return;
            }
            tx.commit();
        }

        delivery.clearAssignment();

        if (delivery.isAssigned() || cargo.getAmountAsInt(0) != 12 || !cargo.getResource(0).equals(ItemResource.of(Items.IRON_INGOT))) {
            helper.fail("Assignment clearing altered cargo");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoPartialDelivery(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();
        ItemStacksResourceHandler destination = new ItemStacksResourceHandler(1);

        cargo.set(0, ItemResource.of(Items.IRON_INGOT), 16);
        destination.set(0, ItemResource.of(Items.IRON_INGOT), 60);

        var result = CourierTransferEngine.transferItems(cargo, destination, 16);

        if (result.itemsMoved() != 4 || result.status() != TransferStatus.PARTIAL || cargo.getAmountAsInt(0) != 12 || destination.getAmountAsInt(0) != 64) {
            helper.fail("Partial delivery did not preserve remaining cargo");
            return;
        }

        helper.succeed();
    }

    public static void courierCargoFailedDelivery(GameTestHelper helper) {
        ItemStacksResourceHandler cargo = cargo();
        ItemStacksResourceHandler destination = new ItemStacksResourceHandler(1);

        cargo.set(0, ItemResource.of(Items.IRON_INGOT), 16);
        destination.set(0, ItemResource.of(Items.GOLD_INGOT), 32);

        var result = CourierTransferEngine.transferItems(cargo, destination, 16);

        if (result.itemsMoved() != 0 || result.status() != TransferStatus.DESTINATION_FULL || cargo.getAmountAsInt(0) != 16 || !cargo.getResource(0).equals(ItemResource.of(Items.IRON_INGOT)) || destination.getAmountAsInt(0) != 32 || !destination.getResource(0).equals(ItemResource.of(Items.GOLD_INGOT))) {
            helper.fail("Rejected delivery changed inventory contents");
            return;
        }

        helper.succeed();
    }

}
