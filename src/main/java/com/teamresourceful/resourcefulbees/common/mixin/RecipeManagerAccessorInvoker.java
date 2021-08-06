package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessorInvoker {

    @Accessor
    Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> getRecipes();

    @Accessor
    void setRecipes(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes);

    @Invoker
    <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> callByType(IRecipeType<T> p_215366_1_);

}
