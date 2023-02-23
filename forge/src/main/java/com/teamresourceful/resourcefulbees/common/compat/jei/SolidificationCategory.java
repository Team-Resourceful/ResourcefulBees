package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SolidificationCategory extends BaseCategory<SolidificationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/solidification.png");
    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "solidification");
    public static final RecipeType<SolidificationRecipe> RECIPE = new RecipeType<>(ID, SolidificationRecipe.class);

    protected SolidificationCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
                JeiTranslations.SOLIDIFICATION,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 46, 61).addPadding(10, 10, 20, 20).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.SOLIDIFICATION_CHAMBER_ITEM.get().getDefaultInstance()));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SolidificationRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 11)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.fluid())
                .setFluidRenderer(recipe.fluid().getAmount(), false, 16, 16)
                .setSlotName("input");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 54)
                .addIngredient(VanillaTypes.ITEM_STACK, recipe.stack())
                .setSlotName("output");
    }

}
