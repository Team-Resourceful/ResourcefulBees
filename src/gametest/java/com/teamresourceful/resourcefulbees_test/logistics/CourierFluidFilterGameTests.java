package com.teamresourceful.resourcefulbees_test.logistics;

import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.logistics.CourierFilter;
import com.teamresourceful.resourcefulbees.common.logistics.CourierFilterEntry;
import com.teamresourceful.resourcefulbees.common.logistics.CourierFilterMode;
import com.teamresourceful.resourcefulbees.common.logistics.CourierFluidTransferEngine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

import java.util.List;

/**
 * Regression tests for the unified item/fluid filter and fluid transfer engine.
 */
public final class CourierFluidFilterGameTests {
    private static final int AMOUNT = 1_000;

    private CourierFluidFilterGameTests() {
    }

    private static CourierFilter filter(CourierFilterMode mode, Fluid... fluids) {
        return new CourierFilter(mode, java.util.Arrays.stream(fluids).map(fluid -> new CourierFilterEntry(BuiltInRegistries.FLUID.getKey(fluid))).toList());
    }

    private static FluidStacksResourceHandler tank(int slots) {
        return new FluidStacksResourceHandler(slots, 4_000);
    }

    private static void fill(FluidStacksResourceHandler handler, int slot, Fluid fluid, int amount) {
        handler.set(slot, FluidResource.of(fluid), amount);
    }

    private static void assertAmount(GameTestHelper helper, FluidStacksResourceHandler handler, int slot, Fluid fluid, int amount) {
        if (handler.getAmountAsInt(slot) != amount) {
            helper.fail("Tank " + slot + ": expected " + amount + ", got " + handler.getAmountAsInt(slot));
            return;
        }
        if (amount > 0 && !handler.getResource(slot).equals(FluidResource.of(fluid))) {
            helper.fail("Tank " + slot + " contains the wrong fluid");
        }
    }

    private static int move(FluidStacksResourceHandler source, FluidStacksResourceHandler destination, CourierFilter filter) {
        return CourierFluidTransferEngine.transferFluids(source, destination, AMOUNT, filter).itemsMoved();
    }

    public static void noneAccepts(GameTestHelper helper) {
        var source = tank(1);
        var destination = tank(1);
        fill(source, 0, Fluids.WATER, AMOUNT);
        if (move(source, destination, CourierFilter.NONE) != AMOUNT) {
            helper.fail("NONE did not transfer water");
            return;
        }
        assertAmount(helper, source, 0, Fluids.WATER, 0);
        assertAmount(helper, destination, 0, Fluids.WATER, AMOUNT);
        helper.succeed();
    }

    public static void whitelistAccepts(GameTestHelper helper) {
        var source = tank(1);
        var destination = tank(1);
        fill(source, 0, Fluids.WATER, AMOUNT);
        if (move(source, destination, filter(CourierFilterMode.WHITELIST, Fluids.WATER)) != AMOUNT) {
            helper.fail("Matching whitelist did not transfer water");
            return;
        }
        assertAmount(helper, destination, 0, Fluids.WATER, AMOUNT);
        helper.succeed();
    }

    public static void whitelistRejects(GameTestHelper helper) {
        var source = tank(1);
        var destination = tank(1);
        fill(source, 0, Fluids.WATER, AMOUNT);
        if (move(source, destination, filter(CourierFilterMode.WHITELIST, Fluids.LAVA)) != 0) {
            helper.fail("Whitelist transferred an unlisted fluid");
            return;
        }
        assertAmount(helper, source, 0, Fluids.WATER, AMOUNT);
        assertAmount(helper, destination, 0, Fluids.WATER, 0);
        helper.succeed();
    }

    public static void blacklistAccepts(GameTestHelper helper) {
        var source = tank(1);
        var destination = tank(1);
        fill(source, 0, Fluids.WATER, AMOUNT);
        if (move(source, destination, filter(CourierFilterMode.BLACKLIST, Fluids.LAVA)) != AMOUNT) {
            helper.fail("Blacklist rejected an unlisted fluid");
            return;
        }
        assertAmount(helper, destination, 0, Fluids.WATER, AMOUNT);
        helper.succeed();
    }

    public static void blacklistRejects(GameTestHelper helper) {
        var source = tank(1);
        var destination = tank(1);
        fill(source, 0, Fluids.WATER, AMOUNT);
        if (move(source, destination, filter(CourierFilterMode.BLACKLIST, Fluids.WATER)) != 0) {
            helper.fail("Blacklist transferred a blocked fluid");
            return;
        }
        assertAmount(helper, source, 0, Fluids.WATER, AMOUNT);
        assertAmount(helper, destination, 0, Fluids.WATER, 0);
        helper.succeed();
    }

    public static void multipleTanks(GameTestHelper helper) {
        var source = tank(2);
        var destination = tank(1);
        fill(source, 0, Fluids.LAVA, AMOUNT);
        fill(source, 1, Fluids.WATER, AMOUNT);
        if (move(source, destination, filter(CourierFilterMode.WHITELIST, Fluids.WATER)) != AMOUNT) {
            helper.fail("Failed to skip lava and transfer water");
            return;
        }
        assertAmount(helper, source, 0, Fluids.LAVA, AMOUNT);
        assertAmount(helper, source, 1, Fluids.WATER, 0);
        assertAmount(helper, destination, 0, Fluids.WATER, AMOUNT);
        helper.succeed();
    }

    public static void sharedFilterMatchesItemsAndFluids(GameTestHelper helper) {
        var itemFilter = new CourierFilter(CourierFilterMode.WHITELIST, List.of(new CourierFilterEntry(BuiltInRegistries.ITEM.getKey(Items.IRON_INGOT))));
        var fluidFilter = filter(CourierFilterMode.WHITELIST, Fluids.WATER);
        if (!itemFilter.allows(Items.IRON_INGOT) || itemFilter.allows(Items.GOLD_INGOT) || !fluidFilter.allows(Fluids.WATER) || fluidFilter.allows(Fluids.LAVA)) {
            helper.fail("Unified CourierFilter matching is inconsistent");
            return;
        }
        helper.succeed();
    }

    public static void codecRoundTrip(GameTestHelper helper) {
        var original = filter(CourierFilterMode.BLACKLIST, Fluids.WATER, Fluids.LAVA);
        var encoded = CourierFilter.CODEC.encodeStart(JsonOps.INSTANCE, original).result().orElse(null);
        if (encoded == null) {
            helper.fail("Filter encoding failed");
            return;
        }
        var restored = CourierFilter.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElse(null);
        if (!original.equals(restored)) {
            helper.fail("Filter codec round-trip changed entries");
            return;
        }
        helper.succeed();
    }
}
