package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class FlowHiveCategory extends BaseCategory<FlowHiveCategory.Recipe> {

    public static final ResourceLocation HIVE_BACK = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/jei/honeycomb.png");
    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "flow_hive");
    public static final RecipeType<FlowHiveCategory.Recipe> RECIPE = new RecipeType<>(ID, FlowHiveCategory.Recipe.class);

    private final IDrawable flowHiveBackground;

    public FlowHiveCategory(IGuiHelper guiHelper) {
        super(guiHelper, RECIPE,
            JeiTranslations.FLOW_HIVE_OUTPUTS,
            guiHelper.createBlankDrawable(160, 26),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.FLOW_HIVE.get().getDefaultInstance()));

        flowHiveBackground = guiHelper.drawableBuilder(HIVE_BACK, 0, 52, 160, 26).addPadding(0, 0, 0, 0).build();
    }

    public static List<FlowHiveCategory.Recipe> getHoneycombRecipes(Collection<FlowHiveRecipe> recipes) {
        return recipes.stream().flatMap(FlowHiveCategory::createRecipes).toList();
    }

    private static Stream<FlowHiveCategory.Recipe> createRecipes(FlowHiveRecipe recipe) {
        List<FlowHiveCategory.Recipe> recipes = new ArrayList<>();

        RecipeFluid honey = recipe.fluid();
        if (!honey.isEmpty()) {
            recipe.bees().stream()
                .filter(Holder::isBound)
                .map(Holder::get)
                .map(entityType -> new FlowHiveCategory.Recipe(honey, entityType))
                .forEach(recipes::add);
        }

        return recipes.stream();
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull FlowHiveCategory.Recipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 5)
            .addFluidStack(recipe.fluid().fluid(), recipe.fluid().amount(), recipe.fluid().tag())
            .setFluidRenderer(recipe.fluid().amount(), false, 16, 16)
            .setSlotName("honey");
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 5)
            .addIngredient(VanillaTypes.ITEM_STACK, ModItems.FLOW_HIVE.get().getDefaultInstance())
            .setSlotName("hive");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 3)
            .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(recipe.bee(), 45.0f))
            .setSlotName("bee");
    }

    @Override
    public void draw(@NotNull FlowHiveCategory.Recipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, view, graphics, mouseX, mouseY);
        flowHiveBackground.draw(graphics);
    }

    public record Recipe(RecipeFluid fluid, EntityType<?> bee) {}
}
