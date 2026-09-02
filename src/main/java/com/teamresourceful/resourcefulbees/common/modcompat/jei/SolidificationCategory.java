package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class SolidificationCategory
        extends BaseCategory<SolidificationRecipe> {

    private static final int WIDTH = 46;
    private static final int HEIGHT = 61;

    public static final Identifier ID =
            ModIdentifier.of("solidification");

    public static final IRecipeType<SolidificationRecipe> RECIPE =
            IRecipeType.create(
                    ID,
                    SolidificationRecipe.class
            );

    private final IPlatformFluidHelper<FluidStack> fluidHelper;

    public SolidificationCategory(
            IGuiHelper guiHelper,
            IPlatformFluidHelper<FluidStack> fluidHelper
    ) {
        super(
                guiHelper,
                RECIPE,
                JeiTranslations.SOLIDIFICATION,
                guiHelper.createDrawableItemLike(
                        ModItems.SOLIDIFICATION_CHAMBER_ITEM.get()
                ),
                WIDTH,
                HEIGHT
        );

        this.fluidHelper = fluidHelper;
    }

    @Override
    public void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull SolidificationRecipe recipe,
            @NotNull IFocusGroup focuses
    ) {
        List<FluidStack> fluids = recipe.fluid()
                .ingredient()
                .fluids()
                .stream()
                .map(holder ->
                        fluidHelper.create(
                                holder,
                                recipe.fluid().amount()
                        )
                )
                .toList();

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        21,
                        11
                )
                .addIngredients(fluidHelper.getFluidIngredientType(), fluids)
                .setFluidRenderer(
                        recipe.fluid().amount(),
                        false,
                        16,
                        16
                )
                .setSlotName("input");

        builder.addSlot(
                        RecipeIngredientRole.OUTPUT,
                        49,
                        54
                )
                .add(recipe.stack())
                .setSlotName("output");
    }

    @Override
    public @Nullable Identifier getIdentifier(
            @NonNull SolidificationRecipe recipe
    ) {
        return null;
    }
}