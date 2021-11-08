package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;

public class RecipeHelper {

    private RecipeHelper() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static ShapedRecipeBuilder getStorageRecipe(IItemProvider result, Ingredient item) {
        return AdvancedShapedRecipeBuilder.shaped(result).pattern("###","###","###").define('#', item).getBuilder();
    }

    public static ShapelessRecipeBuilder getStorageToItemRecipe(IItemProvider result, Ingredient item){
        return ShapelessRecipeBuilder.shapeless(result, 9).requires(item);
    }

    public static AdvancedShapedRecipeBuilder createBoxed(Ingredient middle, Ingredient sides, IItemProvider result) {
        return AdvancedShapedRecipeBuilder
                .shaped(result)
                .define('S', sides).define('M', middle)
                .pattern("SSS", "SMS", "SSS");
    }

    public static AdvancedShapedRecipeBuilder createCornerWithChestRecipe(Ingredient previous, ITag<Item> corners, IItemProvider result) {
        return createCornerWithMid(previous, Ingredient.of(corners), Ingredient.of(net.minecraftforge.common.Tags.Items.CHESTS), result);
    }

    public static AdvancedShapedRecipeBuilder createCornerWithMid(Ingredient middle, Ingredient corners, Ingredient sides, IItemProvider result) {
        return AdvancedShapedRecipeBuilder
                .shaped(result)
                .define('C', corners).define('S', sides).define('M', middle)
                .pattern("CSC", "SMS", "CSC");
    }
}
