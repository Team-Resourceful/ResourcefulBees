package com.teamresourceful.resourcefulbees.testing.recipes;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.config.CentrifugeConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
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

import java.util.ArrayList;
import java.util.Optional;

@GameTestHolder(ModConstants.MOD_ID)
public class CentrifugeTest {

    private static final ResourceLocation TEST_ID = new ResourceLocation(ModConstants.MOD_ID, "centrifuge_test");
    private static final CentrifugeRecipe RECIPE = new CentrifugeRecipe(TEST_ID, Ingredient.of(Items.STICK), 1, new ArrayList<>(), new ArrayList<>(), CentrifugeConfig.defaultCentrifugeRecipeTime, CentrifugeConfig.centrifugeRfPerTick, Optional.empty());
    private static final JsonObject JSON_RECIPE = GsonHelpers.parseJson("""
        {
            "ingredient": {
                "item": "minecraft:stick"
            }
        }
        """).orElseThrow();

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void centrifugeRecipeJsonDecode(GameTestHelper helper) {
        try {
            ModRecipeSerializers.CENTRIFUGE_RECIPE.get().fromJson(TEST_ID, JSON_RECIPE);
        } catch (Exception e) {
            helper.fail("Failed to decode json to correct recipe.");
        }
        helper.succeed();
    }

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void centrifugeRecipeNetEncode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.CENTRIFUGE_RECIPE.get().toNetwork(buf, RECIPE);
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
    public static void centrifugeRecipeNetDecode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.CENTRIFUGE_RECIPE.get().toNetwork(buf, RECIPE);
            if (ModRecipeSerializers.CENTRIFUGE_RECIPE.get().fromNetwork(TEST_ID, buf) == null) {
                helper.fail("Failed to decode json recipe.");
            }
        }catch (Exception e) {
            helper.fail("Failed to decode json recipe.");
        }
        helper.succeed();
    }

}
