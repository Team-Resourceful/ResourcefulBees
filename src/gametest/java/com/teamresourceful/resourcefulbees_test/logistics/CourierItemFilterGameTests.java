package com.teamresourceful.resourcefulbees_test.logistics;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.UUID;

public final class CourierItemFilterGameTests {

    private CourierItemFilterGameTests() {
    }

    private static CourierFilterEntry entry(String id) {
        return new CourierFilterEntry(Identifier.parse(id));
    }

    private static CourierFilter filter(CourierFilterMode mode, String... itemIds) {
        List<CourierFilterEntry> entries = java.util.Arrays.stream(itemIds).map(CourierItemFilterGameTests::entry).toList();

        return new CourierFilter(mode, entries);
    }

    private static CourierRoute route(CourierFilter filter) {
        return new CourierRoute(UUID.randomUUID(), CourierResourceType.ITEM, new CourierEndpoint(new BlockPos(1, 2, 3), Direction.UP), new CourierEndpoint(new BlockPos(5, 2, 3), Direction.UP), true, 0, CourierRouteExecutionMode.COURIER, filter);
    }

    private static void assertAllowed(GameTestHelper helper, CourierFilter filter, Item item, boolean expected) {
        helper.assertTrue(filter.allows(item) == expected, "Unexpected item filter result for " + item);
    }

    public static void noneAllowsItems(GameTestHelper helper) {
        CourierFilter filter = CourierFilter.NONE;

        assertAllowed(helper, filter, Items.IRON_INGOT, true);
        assertAllowed(helper, filter, Items.GOLD_INGOT, true);
        assertAllowed(helper, filter, Items.DIAMOND, true);

        helper.succeed();
    }

    public static void whitelistAcceptsMatching(GameTestHelper helper) {
        CourierFilter filter = filter(CourierFilterMode.WHITELIST, "minecraft:iron_ingot");

        assertAllowed(helper, filter, Items.IRON_INGOT, true);

        helper.succeed();
    }

    public static void whitelistRejectsOther(GameTestHelper helper) {
        CourierFilter filter = filter(CourierFilterMode.WHITELIST, "minecraft:iron_ingot");

        assertAllowed(helper, filter, Items.GOLD_INGOT, false);
        assertAllowed(helper, filter, Items.DIAMOND, false);

        helper.succeed();
    }

    public static void blacklistRejectsMatching(GameTestHelper helper) {
        CourierFilter filter = filter(CourierFilterMode.BLACKLIST, "minecraft:iron_ingot");

        assertAllowed(helper, filter, Items.IRON_INGOT, false);

        helper.succeed();
    }

    public static void blacklistAcceptsOther(GameTestHelper helper) {
        CourierFilter filter = filter(CourierFilterMode.BLACKLIST, "minecraft:iron_ingot");

        assertAllowed(helper, filter, Items.GOLD_INGOT, true);
        assertAllowed(helper, filter, Items.DIAMOND, true);

        helper.succeed();
    }

    public static void emptyWhitelist(GameTestHelper helper) {
        CourierFilter filter = filter(CourierFilterMode.WHITELIST);

        assertAllowed(helper, filter, Items.IRON_INGOT, false);
        assertAllowed(helper, filter, Items.GOLD_INGOT, false);

        // An empty blacklist should allow everything.
        CourierFilter blacklist = filter(CourierFilterMode.BLACKLIST);

        assertAllowed(helper, blacklist, Items.IRON_INGOT, true);

        helper.succeed();
    }

    public static void serializationRoundTrip(GameTestHelper helper) {
        CourierFilter original = filter(CourierFilterMode.WHITELIST, "minecraft:iron_ingot", "minecraft:gold_ingot");

        var encoded = CourierFilter.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();

        CourierFilter decoded = CourierFilter.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        helper.assertTrue(original.equals(decoded), "Item filter changed during serialization");

        assertAllowed(helper, decoded, Items.IRON_INGOT, true);
        assertAllowed(helper, decoded, Items.GOLD_INGOT, true);
        assertAllowed(helper, decoded, Items.DIAMOND, false);

        helper.succeed();
    }

    public static void legacyRouteDefaults(GameTestHelper helper) {
        CourierRoute original = route(CourierFilter.NONE);

        var encoded = CourierRoute.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();

        JsonObject legacyJson = encoded.getAsJsonObject();

        // Simulate an existing saved route without item filtering.
        legacyJson.remove("item_filter");

        CourierRoute decoded = CourierRoute.CODEC.parse(JsonOps.INSTANCE, legacyJson).getOrThrow();

        helper.assertTrue(decoded.courierFilter().equals(CourierFilter.NONE), "Legacy route did not default to unrestricted filtering");

        helper.assertTrue(decoded.id().equals(original.id()) && decoded.source().equals(original.source()) && decoded.destination().equals(original.destination()), "Legacy route lost existing configuration");

        helper.succeed();
    }

    public static void routeFilterRoundTrip(GameTestHelper helper) {
        CourierFilter itemFilter = filter(CourierFilterMode.BLACKLIST, "minecraft:diamond");

        CourierRoute original = route(itemFilter);

        var encoded = CourierRoute.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();

        CourierRoute decoded = CourierRoute.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        helper.assertTrue(original.equals(decoded), "Route configuration changed during serialization");

        helper.assertTrue(decoded.courierFilter().equals(itemFilter), "Route item filter was not preserved");

        assertAllowed(helper, decoded.courierFilter(), Items.DIAMOND, false);
        assertAllowed(helper, decoded.courierFilter(), Items.IRON_INGOT, true);

        helper.succeed();
    }
}
