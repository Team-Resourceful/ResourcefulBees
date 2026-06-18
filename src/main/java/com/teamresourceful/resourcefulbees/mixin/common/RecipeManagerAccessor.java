package com.teamresourceful.resourcefulbees.mixin.common;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {

//    @Accessor
//    Map<RecipeType<?>, Map<Identifier, Recipe<?>>> getRecipes();
//
//    @Accessor
//    void setRecipes(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes);
}
