package com.teamresourceful.resourcefulbees.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class HoneyGenCategory
        extends BaseCategory<HoneyGenRecipe> {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 60;

    private static final Identifier BACKGROUND =
            ModIdentifier.of("textures/gui/jei/honey_gen.png");

    public static final Identifier ID =
            ModIdentifier.of("honey_gen");

    public static final IRecipeType<HoneyGenRecipe> RECIPE =
            IRecipeType.create(
                    ID,
                    HoneyGenRecipe.class
            );

    private final IPlatformFluidHelper<FluidStack> fluidHelper;

    private final IDrawableStatic energyContainer;
    private final IDrawableAnimated energyBar;
    private final IDrawableStatic tank;
    private final IDrawableStatic tankOverlay;

    public HoneyGenCategory(
            IGuiHelper guiHelper,
            IPlatformFluidHelper<FluidStack> fluidHelper
    ) {
        super(
                guiHelper,
                RECIPE,
                Component.literal("Honey Generator"),
                guiHelper.createDrawableItemLike(
                        ModItems.HONEY_GENERATOR_ITEM.get()
                ),
                WIDTH,
                HEIGHT
        );

        this.fluidHelper = fluidHelper;

        this.tank = guiHelper.createDrawable(
                BACKGROUND,
                0,
                0,
                18,
                56
        );

        this.tankOverlay = guiHelper.createDrawable(
                BACKGROUND,
                44,
                0,
                18,
                56
        );

        this.energyContainer = guiHelper.createDrawable(
                BACKGROUND,
                18,
                0,
                14,
                56
        );

        this.energyBar = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(
                        BACKGROUND,
                        32,
                        0,
                        12,
                        54
                ),
                400,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
    }

    @Override
    public void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull HoneyGenRecipe recipe,
            @NotNull IFocusGroup focuses
    ) {
        List<FluidStack> honeyStacks = recipe.honey()
                .fluids()
                .stream()
                .map(holder ->
                        fluidHelper.create(
                                holder,
                                recipe.honeyDrainRate()
                        )
                )
                .toList();

        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        21,
                        4
                )
                .addIngredients(
                        fluidHelper.getFluidIngredientType(),
                        honeyStacks
                )
                .setFluidRenderer(
                        1000,
                        false,
                        16,
                        54
                )
                .setOverlay(
                        tankOverlay,
                        0,
                        0
                )
                .setSlotName("input")
                .addRichTooltipCallback(
                        (view, tooltip) ->
                                tooltip.add(
                                        Component.translatable(
                                                JeiTranslations.DRAIN_RATE,
                                                recipe.honeyDrainRate()
                                        )
                                )
                );
    }

    @Override
    public void draw(
            @NotNull HoneyGenRecipe recipe,
            @NotNull IRecipeSlotsView recipeSlotsView,
            @NotNull GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        tank.draw(
                graphics,
                20,
                3
        );

        energyContainer.draw(
                graphics,
                80,
                3
        );

        energyBar.draw(
                graphics,
                81,
                4
        );
    }

    @Override
    public void getTooltip(
            @NotNull ITooltipBuilder tooltip,
            @NotNull HoneyGenRecipe recipe,
            @NotNull IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseX >= 80
                && mouseX <= 80 + energyContainer.getWidth()
                && mouseY >= 3
                && mouseY <= 3 + energyContainer.getHeight()) {

            tooltip.add(
                    Component.translatable(
                            JeiTranslations.ENERGY,
                            (1000 / recipe.honeyDrainRate())
                                    * recipe.energyFillRate()
                    )
            );

            tooltip.add(
                    Component.translatable(
                            JeiTranslations.FILL_RATE,
                            recipe.energyFillRate()
                    )
            );
        }
    }

    @Override
    public @Nullable Identifier getIdentifier(
            @NonNull HoneyGenRecipe recipe
    ) {
        return null;
    }
}