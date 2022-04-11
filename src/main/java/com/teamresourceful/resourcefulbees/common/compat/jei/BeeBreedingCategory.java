package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//TODO rewrite to be more like centrifuge category with weight and chance but also like mutation for items and entities.
public class BeeBreedingCategory extends BaseCategory<BeeBreedingCategory.BreedingWrapper> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(ResourcefulBees.MOD_ID, "breeding");

    public BeeBreedingCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                TranslationConstants.Jei.BREEDING,
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 150, 118).addPadding(11, 11, 10, 10).build(),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM, ModItems.BREEDER_ITEM.get().getDefaultInstance()),
                BreedingWrapper.class);
    }

    public static List<BreedingWrapper> getRecipes(Collection<Recipe<Container>> recipes) {
        List<BreedingWrapper> wrappers = new ArrayList<>();
        for (Recipe<Container> recipe : recipes) {
            if (recipe instanceof BreederRecipe breederRecipe) {
                breederRecipe.outputs().forEach(output -> wrappers.add(new BreedingWrapper(breederRecipe, output)));
            }
        }
        return wrappers;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull BreedingWrapper recipe, @NotNull IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);

        builder.addSlot(RecipeIngredientRole.INPUT, 11, 22)
                .addIngredients(recipe.ogRecipe().getIngredients().get(0)).setSlotName("parent_1_entity");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 40)
                .addIngredients(recipe.ogRecipe().getIngredients().get(1)).setSlotName("parent_1_item");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 94)
                .addIngredients(recipe.ogRecipe().getIngredients().get(2)).setSlotName("parent_2_entity");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 112)
                .addIngredients(recipe.ogRecipe().getIngredients().get(3)).setSlotName("parent_2_item");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 94)
                .addIngredient(VanillaTypes.ITEM, recipe.output().output()).setSlotName("output_item");

        recipe.parent1.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 30)
                        .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityType, 45f))
                        .setSlotName("parent_1_entity_display"));

        recipe.parent2.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 102)
                        .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityType, 45f))
                        .setSlotName("parent_2_entity_display"));

        recipe.output.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 67)
                        .addIngredient(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityType, 45f))
                        .setSlotName("output_entity"));

    }

    public static record BreedingWrapper(BreederRecipe.BreederPair parent1, BreederRecipe.BreederPair parent2, Optional<Ingredient> input, BreederRecipe.BreederOutput output, BreederRecipe ogRecipe){
        public BreedingWrapper(BreederRecipe recipe, BreederRecipe.BreederOutput output) {
            this(recipe.parent1(), recipe.parent2(), recipe.input(), output, recipe);
        }
    }
}
