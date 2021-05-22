package com.teamresourceful.resourcefulbees.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeUtils {

    private RecipeUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static double getEffectiveWeight(RandomCollection<?> randomCollection, double weight) {
        return randomCollection.getAdjustedWeight(weight);
    }

    public static void setFluidInput(IIngredients ingredients, Tag<?> tagInput, Block blockInput) {
        if (tagInput != null) {
            List<List<FluidStack>> list = new ArrayList<>();
            List<FluidStack> ingredientList = new ArrayList<>();
            tagInput.getValues().stream()
                    .filter(Fluid.class::isInstance)
                    .map(Fluid.class::cast)
                    .forEach(fluid -> ingredientList.add(new FluidStack(fluid, 1000)));
            list.add(ingredientList);
            ingredients.setInputLists(VanillaTypes.FLUID, list);
        } else if (blockInput instanceof LiquidBlock) {
            FluidStack fluidStack = new FluidStack(((LiquidBlock) blockInput).getFluid(), 1000);
            ingredients.setInput(VanillaTypes.FLUID, fluidStack);
        }
    }

    public static void setBlockInput(IIngredients ingredients, Tag<?> tagInput, Block blockInput) {
        if (tagInput != null) {
            List<List<ItemStack>> list = new ArrayList<>();
            List<ItemStack> ingredientList = new ArrayList<>();
            tagInput.getValues().stream()
                    .filter(Block.class::isInstance)
                    .map(Block.class::cast)
                    .forEach(block -> ingredientList.add(new ItemStack(block)));
            list.add(ingredientList);
            ingredients.setInputLists(VanillaTypes.ITEM, list);
        } else {
            ItemStack itemStack = new ItemStack(blockInput);
            ingredients.setInput(VanillaTypes.ITEM, itemStack);
        }
    }

    public static void setBlockOutput(Block block, CompoundTag nbt, IIngredients ingredients) {
        ItemStack itemStack = new ItemStack(block);
        if (!nbt.isEmpty()) {
            itemStack.setTag(nbt);
        }
        ingredients.setOutput(VanillaTypes.ITEM, itemStack);
    }

    public static void setFluidOutput(Block block, CompoundTag nbt, IIngredients ingredients) {
        FluidStack fluidStack = new FluidStack(((LiquidBlock) block).getFluid(), 1000);
        if (!nbt.isEmpty()) {
            fluidStack.setTag(nbt);
        }
        ingredients.setOutput(VanillaTypes.FLUID, fluidStack);
    }

    public static void addMutationToolTip(BlockMutation.Recipe recipe, IGuiItemStackGroup itemStacks) {
        itemStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> addMutationToolTip(slotIndex, recipe, tooltip));
    }

    public static void addMutationToolTip(BlockMutation.Recipe recipe, IGuiFluidStackGroup fluidStacks) {
        fluidStacks.addTooltipCallback((slotIndex, isInputStack, stack, tooltip) -> addMutationToolTip(slotIndex, recipe, tooltip));
    }

    private static void addMutationToolTip(int slotIndex, BlockMutation.Recipe recipe, List<Component> tooltip) {
        if (slotIndex == 0 && !recipe.getBlockOutput().getCompoundNBT().isPresent()) {
            if (Screen.hasShiftDown()) {
                List<String> lore = BeeInfoUtils.getLoreLines(recipe.getBlockOutput().getCompoundNBT().get());
                lore.forEach(l -> tooltip.add(new TextComponent(l).withStyle(Style.EMPTY.withColor(TextColor.parseColor("dark_purple")))));
            } else {
                tooltip.add(new TranslatableComponent("gui.resourcefulbees.jei.tooltip.show_nbt").withStyle(Style.EMPTY.withColor(TextColor.parseColor("dark_purple"))));
            }
        }
    }

    public static IGuiItemStackGroup setGuiItemStacksGroup(IRecipeLayout iRecipeLayout, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(0, false, 65, 48);
        itemStacks.init(1, true, 15, 57);
        itemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        return itemStacks;
    }

    public static List<Component> getTooltipStrings(double mouseX, double mouseY, double chance) {
        double infoX = 63D;
        double infoY = 8D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D) {
            return Collections.singletonList(new TranslatableComponent("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation.info"));
        }
        double info2X = 54;
        double info2Y = 34;
        if (mouseX >= info2X && mouseX <= info2X + 9D && mouseY >= info2Y && mouseY <= info2Y + 9D && chance < 1) {
            return Collections.singletonList(new TranslatableComponent("gui." + ResourcefulBees.MOD_ID + ".jei.category.mutation_chance.info"));
        }
        return Collections.emptyList();
    }

    public static void drawMutationScreen(PoseStack stack, IDrawable beeHive, IDrawable info, double weight, double chance) {
        beeHive.draw(stack, 65, 10);
        info.draw(stack, 63, 8);
        if (weight == 1 && chance == 1) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        if (chance < 1) {
            String chanceString = decimalFormat.format(chance);
            int padding2 = fontRenderer.width(chanceString) / 2;
            info.draw(stack, 54, 34);
            fontRenderer.draw(stack, chanceString, 76F - padding2, 35, 0xff808080);
        }
        if (weight < 1) {
            String weightString = decimalFormat.format(weight);
            int padding = fontRenderer.width(weightString) / 2;
            fontRenderer.draw(stack, weightString, 48F - padding, 66, 0xff808080);
        }
    }
}
