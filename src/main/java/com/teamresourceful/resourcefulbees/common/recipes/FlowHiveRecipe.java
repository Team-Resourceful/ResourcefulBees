package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record FlowHiveRecipe(ResourceLocation id, HolderSet<EntityType<?>> bees, RecipeFluid fluid) implements CodecRecipe<RecipeInput> {

    public static Codec<FlowHiveRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(FlowHiveRecipe::bees),
                RecipeFluid.CODEC.fieldOf("fluid").forGetter(FlowHiveRecipe::fluid)
        ).apply(instance, FlowHiveRecipe::new));
    }

    public static Optional<RecipeHolder<FlowHiveRecipe>> findRecipe(RecipeManager manager, EntityType<?> bee) {
        return manager
                .getAllRecipesFor(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.value().bees().contains(bee.builtInRegistryHolder()))
                .findFirst();
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public CodecRecipeSerializer<? extends CodecRecipe<RecipeInput>> serializer() {
        return ModRecipeSerializers.FLOW_HIVE_RECIPE.get();
    }

    @Override
    public @NotNull
    RecipeType<?> getType() {
        return ModRecipes.FLOW_HIVE_RECIPE_TYPE.get();
    }
}
