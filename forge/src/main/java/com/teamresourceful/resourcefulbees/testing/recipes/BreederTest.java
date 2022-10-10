package com.teamresourceful.resourcefulbees.testing.recipes;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.utils.GsonHelpers;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.Optional;

@GameTestHolder(ResourcefulBees.MOD_ID)
public class BreederTest {

    private static final ResourceLocation TEST_ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeder_test");
    private static final BreederRecipe.BreederPair PAIR = new BreederRecipe.BreederPair(Ingredient.of(Items.STICK), Optional.empty(), Ingredient.of(Items.DIAMOND), Optional.empty());
    private static final BreederRecipe RECIPE = new BreederRecipe(TEST_ID, PAIR, PAIR, Optional.empty(), new WeightedCollection<>(), 1200);
    private static final JsonObject JSON_RECIPE = GsonHelpers.parseJson("""
        {
            "parent1": {
                "parent": {
                    "item": "minecraft:stick"
                },
                "feedItem": {
                    "item": "minecraft:diamond"
                }
            },
            "parent2": {
                "parent": {
                    "item": "minecraft:stick"
                },
                "feedItem": {
                    "item": "minecraft:diamond"
                }
            },
            "outputs": []
        }
        """).orElseThrow();

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void breederRecipeJsonDecode(GameTestHelper helper) {
        try {
            ModRecipeSerializers.BREEDER_RECIPE.get().fromJson(TEST_ID, JSON_RECIPE);
        } catch (Exception e) {
            helper.fail("Failed to decode json to correct recipe.");
        }
        helper.succeed();
    }

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void breederRecipeNetEncode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.BREEDER_RECIPE.get().toNetwork(buf, RECIPE);
            if (buf.readableBytes() <= 0) {
                helper.fail("No bytes were written when encoding to network.");
            }
        }catch (Exception e) {
            helper.fail("Failed to decode encode to network.");
        }
        helper.succeed();
    }

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void breederRecipeNetDecode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.BREEDER_RECIPE.get().toNetwork(buf, RECIPE);
            if (ModRecipeSerializers.BREEDER_RECIPE.get().fromNetwork(TEST_ID, buf) == null) {
                helper.fail("Failed to decode json recipe.");
            }
        }catch (Exception e) {
            helper.fail("Failed to decode json recipe.");
        }
        helper.succeed();
    }

}
