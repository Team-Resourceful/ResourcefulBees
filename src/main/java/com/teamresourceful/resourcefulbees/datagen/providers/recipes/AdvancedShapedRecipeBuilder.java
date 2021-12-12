package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class AdvancedShapedRecipeBuilder {

    private final ShapedRecipeBuilder builder;

    public static AdvancedShapedRecipeBuilder shaped(RegistryObject<Item> result){
        return new AdvancedShapedRecipeBuilder(result.get());
    }

    public static AdvancedShapedRecipeBuilder shaped(ItemLike result){
        return new AdvancedShapedRecipeBuilder(result);
    }

    private AdvancedShapedRecipeBuilder(ItemLike result) {
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

    public AdvancedShapedRecipeBuilder unlockedBy(String id, CriterionTriggerInstance instance) {
        builder.unlockedBy(id, instance);
        return this;
    }

    public AdvancedShapedRecipeBuilder unlockedBy(RecipeCriteria criteria) {
        builder.unlockedBy(criteria.id(), criteria.instance());
        return this;
    }

    public void save(Consumer<FinishedRecipe> recipeConsumer, String modid, String path){
        builder.save(recipeConsumer, new ResourceLocation(modid, path));
    }

    public void save(Consumer<FinishedRecipe> recipeConsumer){
        builder.save(recipeConsumer);
    }

    public ShapedRecipeBuilder getBuilder() {
        return builder;
    }

}
