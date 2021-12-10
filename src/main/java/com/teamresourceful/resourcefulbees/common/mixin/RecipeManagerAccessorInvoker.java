package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessorInvoker {

    @Accessor
    Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> getRecipes();

    @Accessor
    void setRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes);

    @Invoker
    <C extends Container, T extends Recipe<C>> Map<ResourceLocation, Recipe<C>> callByType(RecipeType<T> recipeType);

}
