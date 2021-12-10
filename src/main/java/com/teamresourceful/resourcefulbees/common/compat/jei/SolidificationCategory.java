package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SolidificationCategory extends BaseCategory<SolidificationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/solidification.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "solidification");

    protected SolidificationCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                ID,
                TranslationConstants.Jei.SOLIDIFICATION,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 46, 61).addPadding(10, 10, 20, 20).build(),
                guiHelper.createDrawableIngredient(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get().getDefaultInstance()),
                SolidificationRecipe.class);
    }

    @Override
    public void setIngredients(@NotNull SolidificationRecipe recipe, @NotNull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.FLUID, recipe.getFluid());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getStack());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, @NotNull SolidificationRecipe recipe, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<ItemStack> guiItemStacks = layout.getIngredientsGroup(VanillaTypes.ITEM);
        IGuiIngredientGroup<FluidStack> guiFluidStacks = layout.getIngredientsGroup(VanillaTypes.FLUID);
        guiFluidStacks.init(0, true, 21, 11);
        guiItemStacks.init(0, false, 48, 53);
        guiFluidStacks.addTooltipCallback((index, isInput, ingredient, tooltip) -> tooltip.add(new TextComponent("Amount: " + recipe.getFluid().getAmount() + "mb")));
        guiFluidStacks.set(ingredients);
        guiItemStacks.set(ingredients);
    }


}
