package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class RecipeHelper {

    private RecipeHelper() {
        throw new UtilityClassError();
    }

    public static ShapedRecipeBuilder getStorageRecipe(ItemLike result, Ingredient item) {
        return AdvancedShapedRecipeBuilder.shaped(result).pattern("###","###","###").define('#', item).getBuilder();
    }

    public static ShapelessRecipeBuilder getStorageToItemRecipe(ItemLike result, Ingredient item){
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result, 9).requires(item);
    }

    public static AdvancedShapedRecipeBuilder createBoxed(Ingredient middle, Ingredient sides, ItemLike result) {
        return AdvancedShapedRecipeBuilder
                .shaped(result)
                .define('S', sides).define('M', middle)
                .pattern("SSS", "SMS", "SSS");
    }

    public static AdvancedShapedRecipeBuilder createCornerWithMid(Ingredient middle, Ingredient corners, Ingredient sides, ItemLike result) {
        return AdvancedShapedRecipeBuilder
                .shaped(result)
                .define('C', corners).define('S', sides).define('M', middle)
                .pattern("CSC", "SMS", "CSC");
    }
}
