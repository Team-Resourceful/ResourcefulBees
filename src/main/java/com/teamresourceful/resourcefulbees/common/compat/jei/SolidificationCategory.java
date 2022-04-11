package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SolidificationCategory extends BaseCategory<SolidificationRecipe> {

    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/solidification.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "solidification");

    protected SolidificationCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                ID,
                TranslationConstants.Jei.SOLIDIFICATION,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 46, 61).addPadding(10, 10, 20, 20).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM, ModItems.SOLIDIFICATION_CHAMBER_ITEM.get().getDefaultInstance()),
                SolidificationRecipe.class);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SolidificationRecipe recipe, @NotNull IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 11)
                .addIngredient(VanillaTypes.FLUID, recipe.fluid())
                .setFluidRenderer(recipe.fluid().getAmount(), true, 16, 16)
                .setSlotName("input");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 54)
                .addIngredient(VanillaTypes.ITEM, recipe.stack())
                .setSlotName("output");
    }

}
