package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HoneyGenCategory extends BaseCategory<HoneyGenRecipe> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/honey_gen.png");
    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "honey_gen");
    public static final RecipeType<HoneyGenRecipe> RECIPE = new RecipeType<>(ID, HoneyGenRecipe.class);

    private final IDrawableStatic energyContainer;
    private final IDrawableAnimated energyBar;
    private final IDrawableStatic tank;
    private final IDrawableStatic tankOverlay;

    protected HoneyGenCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
                Component.literal("Honey Generator"),
                guiHelper.createBlankDrawable(120, 60),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.HONEY_GENERATOR_ITEM.get().getDefaultInstance()));

        tank = guiHelper.createDrawable(BACKGROUND, 0,0, 18, 56);
        tankOverlay = guiHelper.createDrawable(BACKGROUND, 44, 0, 18, 56);
        energyContainer = guiHelper.createDrawable(BACKGROUND, 18, 0, 14, 56);
        energyBar = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(BACKGROUND, 32, 0, 12, 54), 400, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HoneyGenRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 4)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.honey())
                .setFluidRenderer(1000, false, 16, 54)
                .setOverlay(tankOverlay, 0, 0)
                .setSlotName("input")
                .addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable(JeiTranslations.DRAIN_RATE, recipe.honeyDrainRate())));
    }

    @Override
    public void draw(@NotNull HoneyGenRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        tank.draw(graphics, 20, 3);
        energyContainer.draw(graphics, 80, 3);
        energyBar.draw(graphics, 81, 4);
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull HoneyGenRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {

        List<Component> tooltip = new ArrayList<>();

        if (mouseX >= 80 && mouseX <= 80 + energyContainer.getWidth() && mouseY >= 3 && mouseY <= 3 + energyContainer.getHeight()) {
            tooltip.add(Component.translatable(JeiTranslations.ENERGY, (1000 / recipe.honeyDrainRate()) * recipe.energyFillRate()));
            tooltip.add(Component.translatable(JeiTranslations.FILL_RATE, recipe.energyFillRate()));
        }

        return tooltip;
    }
}
