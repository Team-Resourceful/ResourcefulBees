package com.teamresourceful.resourcefulbees.common.recipe.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.lib.builders.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.builders.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import com.teamresourceful.resourcefullib.common.item.OptionalItemStack;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HiveRecipe(ResourceLocation id, HolderSet<EntityType<?>> bees, Map<BeehiveTier, ItemStack> hiveCombs, Map<ApiaryTier, ItemStack> apiaryCombs) implements CodecRecipe<Container> {

    public static Codec<HiveRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                HolderSetCodec.of(Registry.ENTITY_TYPE).fieldOf("bees").forGetter(HiveRecipe::bees),
                Codec.unboundedMap(BeehiveTier.CODEC, ItemStackCodec.CODEC).fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::hiveCombs),
                Codec.unboundedMap(ApiaryTier.CODEC, ItemStackCodec.CODEC).fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::apiaryCombs)
        ).apply(instance, HiveRecipe::new));
    }

    private static Optional<HiveRecipe> findRecipe(RecipeManager manager, EntityType<?> bee) {
        return manager
                .getAllRecipesFor(ModRecipeTypes.HIVE_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.bees().contains(bee.builtInRegistryHolder())).findFirst();
    }

    public static Optional<ItemStack> getHiveOutput(BeehiveTier tier, Entity entity) {
        Optional<HiveRecipe> recipe = findRecipe(entity.level.getRecipeManager(), entity.getType());
        return OptionalItemStack.ofNullable(recipe.map(t -> t.getHiveOutput(tier)).orElseGet(() -> {
            if (entity instanceof BeeCompat compat) {
                return compat.getHiveOutput(tier);
            }
            return ItemStack.EMPTY;
        }));
    }

    public static Optional<ItemStack> getApiaryOutput(ApiaryTier tier, Entity entity) {
        Optional<HiveRecipe> recipe = findRecipe(entity.level.getRecipeManager(), entity.getType());
        return OptionalItemStack.ofNullable(recipe.map(t -> t.getApiaryOutput(tier)).orElseGet(() -> {
            if (entity instanceof BeeCompat compat) {
                return compat.getApiaryOutput(tier);
            }
            return ItemStack.EMPTY;
        }));
    }


    public ItemStack getHiveOutput(BeehiveTier tier) {
        return hiveCombs().getOrDefault(tier, ItemStack.EMPTY).copy();
    }

    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return apiaryCombs().getOrDefault(tier, ItemStack.EMPTY).copy();
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level world) {
        return false;
    }

    @Override
    public @NotNull
    RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HIVE_RECIPE.get();
    }

    @Override
    public @NotNull
    RecipeType<?> getType() {
        return ModRecipeTypes.HIVE_RECIPE_TYPE.get();
    }
}
