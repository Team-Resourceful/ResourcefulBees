package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record HoneyGenRecipe(ResourceLocation id, RecipeFluid honey, int energyFillRate, int honeyDrainRate) implements CodecRecipe<Container> {

    public static Codec<HoneyGenRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RecipeFluid.CODEC.fieldOf("honey").forGetter(HoneyGenRecipe::honey),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("energyFillRate").orElse(125).forGetter(HoneyGenRecipe::energyFillRate),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("honeyDrainRate").orElse(5).forGetter(HoneyGenRecipe::honeyDrainRate)
        ).apply(instance, HoneyGenRecipe::new));
    }

    public static Optional<HoneyGenRecipe> findRecipe(RecipeManager manager, Fluid fluid, CompoundTag tag) {
        return manager.getAllRecipesFor(ModRecipes.HONEY_GEN_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.honey.matches(fluid, tag))
                .findFirst();
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HONEY_GEN_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.HONEY_GEN_RECIPE_TYPE.get();
    }
}
