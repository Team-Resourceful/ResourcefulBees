package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record FlowHiveRecipe(ResourceLocation id, HolderSet<EntityType<?>> bees, RecipeFluid fluid) implements CodecRecipe<Container> {

    public static Codec<FlowHiveRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(FlowHiveRecipe::bees),
                RecipeFluid.CODEC.fieldOf("fluid").forGetter(FlowHiveRecipe::fluid)
        ).apply(instance, FlowHiveRecipe::new));
    }

    public static Optional<FlowHiveRecipe> findRecipe(RecipeManager manager, EntityType<?> bee) {
        return manager
                .getAllRecipesFor(ModRecipes.FLOW_HIVE_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.bees().contains(bee.builtInRegistryHolder()))
                .findFirst();
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level world) {
        return false;
    }

    @Override
    public @NotNull
    RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FLOW_HIVE_RECIPE.get();
    }

    @Override
    public @NotNull
    RecipeType<?> getType() {
        return ModRecipes.FLOW_HIVE_RECIPE_TYPE.get();
    }
}
