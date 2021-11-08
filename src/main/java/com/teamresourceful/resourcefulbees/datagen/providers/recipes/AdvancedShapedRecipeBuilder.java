package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;

public class AdvancedShapedRecipeBuilder {

    private final ShapedRecipeBuilder builder;

    public static AdvancedShapedRecipeBuilder shaped(RegistryObject<Item> result){
        return new AdvancedShapedRecipeBuilder(result.get());
    }

    public static AdvancedShapedRecipeBuilder shaped(IItemProvider result){
        return new AdvancedShapedRecipeBuilder(result);
    }

    private AdvancedShapedRecipeBuilder(IItemProvider result) {
        this.builder = ShapedRecipeBuilder.shaped(result);
    }

    public AdvancedShapedRecipeBuilder pattern(String... pattern) {
        for (String s : pattern) builder.pattern(s);
        return this;
    }

    public AdvancedShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        builder.define(symbol, ingredient);
        return this;
    }

    public AdvancedShapedRecipeBuilder unlockedBy(String id, ICriterionInstance instance) {
        builder.unlockedBy(id, instance);
        return this;
    }

    public AdvancedShapedRecipeBuilder unlockedBy(RecipeCriteria criteria) {
        builder.unlockedBy(criteria.getId(), criteria.getInstance());
        return this;
    }

    public void save(Consumer<IFinishedRecipe> recipeConsumer, String modid, String path){
        builder.save(recipeConsumer, new ResourceLocation(modid, path));
    }

    public void save(Consumer<IFinishedRecipe> recipeConsumer){
        builder.save(recipeConsumer);
    }

    public ShapedRecipeBuilder getBuilder() {
        return builder;
    }

}
