
package com.teamresourceful.resourcefulbees.gametest.recipes;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ParentInput;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;

import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public final class BreederTests {

    private BreederTests() {}

    private static BreederRecipe createRecipe() {
        ParentInput firstParent = new ParentInput(
            Ingredient.of(Items.STICK),
            Optional.empty(),
            1,
            Ingredient.of(Items.WHEAT),
            Optional.empty()
        );

        ParentInput secondParent = new ParentInput(
            Ingredient.of(Items.BLAZE_ROD),
            Optional.empty(),
            1,
            Ingredient.of(Items.CARROT),
            Optional.empty()
        );

        return new BreederRecipe(
            firstParent,
            secondParent,
            Ingredient.of(Items.DIAMOND),
            new WeightedCollection<>(),
            1200
        );
    }

    public static void breederRecipeMatchesParents(GameTestHelper helper) {
        BreederRecipe recipe = createRecipe();

        BreederRecipe.Input input = new BreederRecipe.Input(
            new ItemStack(Items.STICK),
            new ItemStack(Items.WHEAT),
            new ItemStack(Items.BLAZE_ROD),
            new ItemStack(Items.CARROT),
            new ItemStack(Items.DIAMOND)
        );

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Recipe should match correct parents and feed");

        helper.succeed();
    }

    public static void breederRecipeMatchesSwappedParents(GameTestHelper helper) {
        BreederRecipe recipe = createRecipe();

        BreederRecipe.Input input = new BreederRecipe.Input(
            new ItemStack(Items.BLAZE_ROD),
            new ItemStack(Items.CARROT),
            new ItemStack(Items.STICK),
            new ItemStack(Items.WHEAT),
            new ItemStack(Items.DIAMOND)
        );

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Recipe should match with swapped parents");

        helper.succeed();
    }

    public static void breederRecipeRequiresOptionalInput(GameTestHelper helper) {
        BreederRecipe recipe = createRecipe();

        // Correct parents and feed, but no diamond.
        BreederRecipe.Input input = new BreederRecipe.Input(
            new ItemStack(Items.STICK),
            new ItemStack(Items.WHEAT),
            new ItemStack(Items.BLAZE_ROD),
            new ItemStack(Items.CARROT),
            ItemStack.EMPTY
        );

        helper.assertFalse(recipe.matches(input, helper.getLevel()), "Recipe must reject missing optional ingredient");

        helper.succeed();
    }


    private static final String JSON_RECIPE = """
    {
      "parent1": {
        "parent": "minecraft:stick",
        "feedItems": "minecraft:wheat",
        "feedAmount": 2
      },
      "parent2": {
        "parent": "minecraft:blaze_rod",
        "feedItems": "minecraft:carrot",
        "feedAmount": 3
      },
      "optional": "minecraft:diamond",
      "outputs": [
        {
          "child": {
            "id": "minecraft:honeycomb",
            "count": 2
          },
          "weight": 1.0,
          "chance": 1.0
        }
      ],
      "time": 1200
    }
    """;

    public static void breederRecipeJsonDecode(GameTestHelper helper) {
        var json = JsonParser.parseString(JSON_RECIPE);

        var ops = RegistryOps.create(
                JsonOps.INSTANCE,
                helper.getLevel().registryAccess()
        );

        BreederRecipe recipe = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, json)
                .getOrThrow();

        helper.assertTrue(
                recipe.time() == 1200,
                "Expected breeding time of 1200 ticks"
        );

        helper.assertTrue(
                recipe.parent1().feedAmount() == 2,
                "First parent should require 2 feed items"
        );

        helper.assertTrue(
                recipe.parent2().feedAmount() == 3,
                "Second parent should require 3 feed items"
        );

        helper.assertTrue(
                recipe.parent1().parent().test(
                        new ItemStack(Items.STICK)
                ),
                "First parent should accept sticks"
        );

        helper.assertTrue(
                recipe.parent2().parent().test(
                        new ItemStack(Items.BLAZE_ROD)
                ),
                "Second parent should accept blaze rods"
        );

        helper.assertTrue(
                recipe.optional().test(
                        new ItemStack(Items.DIAMOND)
                ),
                "Optional ingredient should accept diamonds"
        );

        helper.succeed();
    }


    public static void breederRecipeNetworkEncode(GameTestHelper helper) {
        BreederRecipe recipe = createRecipe();

        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(
                        Unpooled.buffer(),
                        helper.getLevel().registryAccess()
                );

        try {
            BreederRecipe.STREAM_CODEC.encode(
                    buffer,
                    recipe
            );

            helper.assertTrue(
                    buffer.readableBytes() > 0,
                    "Network encoding should write bytes"
            );
        } finally {
            buffer.release();
        }

        helper.succeed();
    }


    public static void breederRecipeNetworkRoundTrip(GameTestHelper helper) {
        BreederRecipe original = createRecipe();

        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(
                        Unpooled.buffer(),
                        helper.getLevel().registryAccess()
                );

        try {
            BreederRecipe.STREAM_CODEC.encode(
                    buffer,
                    original
            );

            BreederRecipe decoded =
                    BreederRecipe.STREAM_CODEC.decode(buffer);

            helper.assertTrue(
                    decoded.time() == original.time(),
                    "Breeding time changed during network serialization"
            );

            helper.assertTrue(
                    decoded.parent1().feedAmount()
                            == original.parent1().feedAmount(),
                    "First parent feed amount changed"
            );

            helper.assertTrue(
                    decoded.parent2().feedAmount()
                            == original.parent2().feedAmount(),
                    "Second parent feed amount changed"
            );

            helper.assertTrue(
                    decoded.parent1().parent().test(
                            new ItemStack(Items.STICK)
                    ),
                    "First parent ingredient was not preserved"
            );

            helper.assertTrue(
                    decoded.parent2().parent().test(
                            new ItemStack(Items.BLAZE_ROD)
                    ),
                    "Second parent ingredient was not preserved"
            );

            helper.assertTrue(
                    decoded.optional().test(
                            new ItemStack(Items.DIAMOND)
                    ),
                    "Optional ingredient was not preserved"
            );

            helper.assertTrue(
                    buffer.readableBytes() == 0,
                    "Decoder did not consume the complete buffer"
            );
        } finally {
            buffer.release();
        }

        helper.succeed();
    }


    private static BreederRecipe createRecipeWithOutputs(GameTestHelper helper) {
        var json = JsonParser.parseString(JSON_RECIPE);

        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());

        return BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, json)
                .getOrThrow();
    }


    public static void breederRecipeJsonRoundTrip(GameTestHelper helper) {
        BreederRecipe original = createRecipeWithOutputs(helper);

        var ops = RegistryOps.create(
                JsonOps.INSTANCE,
                helper.getLevel().registryAccess()
        );

        // Encode the recipe into JSON.
        var encoded = BreederRecipe.MAP_CODEC
                .codec()
                .encodeStart(ops, original)
                .getOrThrow();

        helper.assertTrue(
                encoded.isJsonObject(),
                "Encoded recipe should be a JSON object"
        );

        helper.assertTrue(
                encoded.getAsJsonObject().has("outputs"),
                "Encoded recipe should contain outputs"
        );

        // Decode the generated JSON.
        BreederRecipe decoded = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, encoded)
                .getOrThrow();

        helper.assertTrue(
                decoded.time() == original.time(),
                "Breeding time changed during JSON round-trip"
        );

        helper.assertTrue(
                decoded.parent1().feedAmount()
                        == original.parent1().feedAmount(),
                "First parent feed amount changed"
        );

        helper.assertTrue(
                decoded.parent2().feedAmount()
                        == original.parent2().feedAmount(),
                "Second parent feed amount changed"
        );

        helper.assertTrue(
                decoded.optional().test(new ItemStack(Items.DIAMOND)),
                "Optional ingredient was not preserved"
        );

        ItemStack output = decoded.assemble(
                new BreederRecipe.Input(
                        new ItemStack(Items.STICK),
                        new ItemStack(Items.WHEAT),
                        new ItemStack(Items.BLAZE_ROD),
                        new ItemStack(Items.CARROT),
                        new ItemStack(Items.DIAMOND)
                )
        );

        helper.assertTrue(
                output.is(Items.HONEYCOMB) && output.getCount() == 2,
                "JSON round-trip should preserve the honeycomb output"
        );

        helper.succeed();
    }

    public static void breederRecipeNetworkPreservesOutputs(GameTestHelper helper) {
        var json = JsonParser.parseString(JSON_RECIPE)
                .getAsJsonObject();

        var output = json.getAsJsonArray("outputs")
                .get(0)
                .getAsJsonObject();

        output.addProperty("weight", 2.5);
        output.addProperty("chance", 0.35);

        var ops = RegistryOps.create(
                JsonOps.INSTANCE,
                helper.getLevel().registryAccess()
        );

        BreederRecipe original = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, json)
                .getOrThrow();

        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(
                        Unpooled.buffer(),
                        helper.getLevel().registryAccess()
                );

        try {
            BreederRecipe.STREAM_CODEC.encode(
                    buffer,
                    original
            );

            BreederRecipe decoded =
                    BreederRecipe.STREAM_CODEC.decode(buffer);

            helper.assertTrue(
                    decoded.time() == 1200,
                    "Breeding time should survive serialization"
            );

            // One output makes assemble() deterministic.
            ItemStack result = decoded.assemble(
                    new BreederRecipe.Input(
                            new ItemStack(Items.STICK),
                            new ItemStack(Items.WHEAT),
                            new ItemStack(Items.BLAZE_ROD),
                            new ItemStack(Items.CARROT),
                            new ItemStack(Items.DIAMOND)
                    )
            );

            helper.assertTrue(
                    result.is(Items.HONEYCOMB),
                    "Child output item should be honeycomb"
            );

            helper.assertTrue(
                    result.getCount() == 2,
                    "Child output count should be preserved"
            );

            // Re-encode decoded recipe as JSON to inspect
            // the weighted output metadata.
            var decodedJson = BreederRecipe.MAP_CODEC
                    .codec()
                    .encodeStart(ops, decoded)
                    .getOrThrow()
                    .getAsJsonObject();

            var decodedOutput = decodedJson
                    .getAsJsonArray("outputs")
                    .get(0)
                    .getAsJsonObject();

            helper.assertTrue(
                    Math.abs(
                            decodedOutput.get("weight").getAsDouble() - 2.5
                    ) < 0.000001,
                    "Child output weight was not preserved"
            );

            helper.assertTrue(
                    Math.abs(
                            decodedOutput.get("chance").getAsDouble() - 0.35
                    ) < 0.000001,
                    "Child output chance was not preserved"
            );

            helper.assertTrue(
                    buffer.readableBytes() == 0,
                    "Decoder should consume the complete buffer"
            );
        } finally {
            buffer.release();
        }

        helper.succeed();
    }

    public static void breederRecipeRejectsInvalidTime(GameTestHelper helper) {
        var ops = RegistryOps.create(
                JsonOps.INSTANCE,
                helper.getLevel().registryAccess()
        );

        // Below the minimum.
        var tooShort = JsonParser.parseString(JSON_RECIPE)
                .getAsJsonObject();

        tooShort.addProperty("time", 99);

        var shortResult = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, tooShort);

        helper.assertTrue(
                shortResult.error().isPresent(),
                "Recipe should reject time below 100 ticks"
        );

        // Above the maximum.
        var tooLong = JsonParser.parseString(JSON_RECIPE)
                .getAsJsonObject();

        tooLong.addProperty("time", 72001);

        var longResult = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, tooLong);

        helper.assertTrue(
                longResult.error().isPresent(),
                "Recipe should reject time above 72000 ticks"
        );

        // Verify both valid boundaries.
        for (int time : new int[]{100, 72000}) {
            var validJson = JsonParser.parseString(JSON_RECIPE)
                    .getAsJsonObject();

            validJson.addProperty("time", time);

            BreederRecipe recipe = BreederRecipe.MAP_CODEC
                    .codec()
                    .parse(ops, validJson)
                    .getOrThrow();

            helper.assertTrue(
                    recipe.time() == time,
                    "Valid boundary time should be preserved: " + time
            );
        }

        helper.succeed();
    }

    public static void breederRecipeDefaultsMissingTime(GameTestHelper helper) {
        var json = JsonParser.parseString(JSON_RECIPE)
                .getAsJsonObject();

        json.remove("time");

        var ops = RegistryOps.create(
                JsonOps.INSTANCE,
                helper.getLevel().registryAccess()
        );

        BreederRecipe recipe = BreederRecipe.MAP_CODEC
                .codec()
                .parse(ops, json)
                .getOrThrow();

        helper.assertTrue(
                recipe.time() == BreederConstants.DEFAULT_BREEDER_TIME,
                "Missing time should use the default breeding time"
        );

        helper.succeed();
    }
}
