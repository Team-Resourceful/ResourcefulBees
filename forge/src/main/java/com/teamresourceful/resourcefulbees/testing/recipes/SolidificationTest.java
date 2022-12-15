package com.teamresourceful.resourcefulbees.testing.recipes;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefullib.common.utils.GsonHelpers;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ModConstants.MOD_ID)
public class SolidificationTest {

    private static final ResourceLocation TEST_ID = new ResourceLocation(ModConstants.MOD_ID, "solidification_test");
    private static final SolidificationRecipe RECIPE = new SolidificationRecipe(TEST_ID, new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.STICK));
    private static final JsonObject JSON_RECIPE = GsonHelpers.parseJson("""
        {
            "fluid": {
                "id": "minecraft:water",
                "amount": 1000
            },
            "result": "minecraft:stick"
        }
        """).orElseThrow();

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void solidificationRecipeJsonDecode(GameTestHelper helper) {
        try {
            ModRecipeSerializers.SOLIDIFICATION_RECIPE.get().fromJson(TEST_ID, JSON_RECIPE);
        } catch (Exception e) {
            helper.fail("Failed to decode json to correct recipe.");
        }
        helper.succeed();
    }

    @PrefixGameTestTemplate(value = false)
    @GameTest(template = "blank")
    public static void solidificationRecipeNetEncode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.SOLIDIFICATION_RECIPE.get().toNetwork(buf, RECIPE);
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
    public static void solidificationRecipeNetDecode(GameTestHelper helper) {
        try {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            ModRecipeSerializers.SOLIDIFICATION_RECIPE.get().toNetwork(buf, RECIPE);
            if (ModRecipeSerializers.SOLIDIFICATION_RECIPE.get().fromNetwork(TEST_ID, buf) == null) {
                helper.fail("Failed to decode json recipe.");
            }
        }catch (Exception e) {
            helper.fail("Failed to decode json recipe.");
        }
        helper.succeed();
    }

}
