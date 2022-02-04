package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.compat.jei.ingredients.EntityIngredient;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
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
    public void setIngredients(@NotNull BreedingWrapper recipe, @NotNull IIngredients ingredients) {
        List<EntityIngredient> entities = new ArrayList<>();
        recipe.parent1.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> entities.add(new EntityIngredient(entityType, 45f)));
        recipe.parent2.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> entities.add(new EntityIngredient(entityType, 45f)));
        recipe.output.displayEntity().flatMap(BeeInfoUtils::getOptionalEntityType)
                .ifPresent(entityType -> ingredients.setOutput(JEICompat.ENTITY_INGREDIENT, new EntityIngredient(entityType, 45f)));
        ingredients.setInputs(JEICompat.ENTITY_INGREDIENT, entities);
        ingredients.setInputIngredients(recipe.ogRecipe().getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output.output());
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull BreedingWrapper recipe, @NotNull IIngredients ingredients) {
        var itemGroup = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        itemGroup.init(0, true, 10, 21);
        itemGroup.init(1, true, 10, 39);
        itemGroup.init(2, true, 10, 93);
        itemGroup.init(3, true, 10, 111);
        itemGroup.init(4, false, 137, 93);
        itemGroup.set(ingredients);
        var entityGroup = recipeLayout.getIngredientsGroup(JEICompat.ENTITY_INGREDIENT);
        recipe.parent1.displayEntity().ifPresent(i -> entityGroup.init(0, true, 55, 29));
        recipe.parent2.displayEntity().ifPresent(i -> entityGroup.init(1, true, 55, 101));
        recipe.output.displayEntity().ifPresent(i -> entityGroup.init(2, false, 137, 66));
        entityGroup.set(ingredients);
    }

    public static record BreedingWrapper(BreederRecipe.BreederPair parent1, BreederRecipe.BreederPair parent2, Optional<Ingredient> input, BreederRecipe.BreederOutput output, BreederRecipe ogRecipe){
        public BreedingWrapper(BreederRecipe recipe, BreederRecipe.BreederOutput output) {
            this(recipe.parent1(), recipe.parent2(), recipe.input(), output, recipe);
        }
    }
}
