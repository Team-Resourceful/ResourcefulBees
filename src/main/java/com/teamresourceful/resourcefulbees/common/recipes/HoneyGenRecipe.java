package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record HoneyGenRecipe(ResourceLocation id, RecipeFluid honey, int energyFillRate, int honeyDrainRate) implements CodecRecipe<RecipeInput> {

    public static Codec<HoneyGenRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RecipeFluid.CODEC.fieldOf("honey").forGetter(HoneyGenRecipe::honey),
                CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("energyFillRate", 125).forGetter(HoneyGenRecipe::energyFillRate),
                CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("honeyDrainRate", 5).forGetter(HoneyGenRecipe::honeyDrainRate)
        ).apply(instance, HoneyGenRecipe::new));
    }

    public static Optional<RecipeHolder<HoneyGenRecipe>> findRecipe(RecipeManager manager, Fluid fluid, CompoundTag tag) {
        return manager.getAllRecipesFor(ModRecipes.HONEY_GEN_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.value().honey.matches(fluid, tag))
                .findFirst();
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public CodecRecipeSerializer<? extends CodecRecipe<RecipeInput>> serializer() {
        return ModRecipeSerializers.HONEY_GEN_RECIPE.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.HONEY_GEN_RECIPE_TYPE.get();
    }
}
