//package com.teamresourceful.resourcefulbees.mixin.common;
//
//import com.teamresourceful.resourcefulbees.common.recipes.MutationRecipe;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.Container;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeInput;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.item.crafting.RecipeType;
//import org.jspecify.annotations.NonNull;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.gen.Invoker;
//
//import java.util.Map;
//
//@Mixin(RecipeManager.class)
//public interface RecipeManagerInvoker {
//
//    @Invoker("byType")
//    <C extends Container & RecipeInput, T extends Recipe<@NonNull C>> Map<Identifier, T> callByType(RecipeType<MutationRecipe> recipeType);
//}
