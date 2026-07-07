package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.recipes.inputs.BeeEntityRecipeInput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import com.teamresourceful.resourcefullib.common.item.OptionalItemStack;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HiveRecipe(
        HolderSet<EntityType<?>> bees,
        Map<BeehiveTier, ItemStackTemplate> hiveCombs,
        Map<ApiaryTier, ItemStackTemplate> apiaryCombs
) implements Recipe<BeeEntityRecipeInput> {

    public static final MapCodec<HiveRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(hiveRecipeInstance -> hiveRecipeInstance.group(
            HolderSetCodec.of(BuiltInRegistries.ENTITY_TYPE).fieldOf("bees").forGetter(HiveRecipe::bees),
            Codec.unboundedMap(BeehiveTier.CODEC, ItemStackTemplate.CODEC).fieldOf("hiveCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::hiveCombs),
            Codec.unboundedMap(ApiaryTier.CODEC, ItemStackTemplate.CODEC).fieldOf("apiaryCombs").orElseGet(HashMap::new).forGetter(HiveRecipe::apiaryCombs)
    ).apply(hiveRecipeInstance, HiveRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HiveRecipe> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.holderSet(Registries.ENTITY_TYPE),
                    HiveRecipe::bees,

                    ByteBufCodecs.map(
                            HashMap::new,
                            BeehiveTier.STREAM_CODEC,
                            ItemStackTemplate.STREAM_CODEC
                    ),
                    HiveRecipe::hiveCombs,

                    ByteBufCodecs.map(
                            HashMap::new,
                            ApiaryTier.STREAM_CODEC,
                            ItemStackTemplate.STREAM_CODEC
                    ),
                    HiveRecipe::apiaryCombs,

                    HiveRecipe::new
            );

    private static Optional<RecipeHolder<HiveRecipe>> findRecipe(RecipeManager manager, EntityType<?> bee, Level level) {
        return manager.getRecipeFor(ModRecipes.HIVE_RECIPE_TYPE.get(), new BeeEntityRecipeInput(bee.builtInRegistryHolder()), level);
    }

    public static Optional<ItemStack> getHiveOutput(BeehiveTier tier, Entity entity) {
        Optional<RecipeHolder<HiveRecipe>> recipe = findRecipe((RecipeManager) entity.level().recipeAccess(), entity.getType(), entity.level());
        return OptionalItemStack.ofNullable(recipe.map(t -> t.value().getHiveOutput(tier)).orElseGet(() -> {
            if (entity instanceof BeeCompat compat) {
                return compat.resourcefulBees$getHiveOutput(tier);
            }
            return ItemStack.EMPTY;
        }));
    }

    public static Optional<ItemStack> getApiaryOutput(ApiaryTier tier, Entity entity) {
        Optional<RecipeHolder<HiveRecipe>> recipe = findRecipe((RecipeManager) entity.level().recipeAccess(), entity.getType(), entity.level());
        return OptionalItemStack.ofNullable(recipe.map(t -> t.value().getApiaryOutput(tier)).orElseGet(() -> {
            if (entity instanceof BeeCompat compat) {
                return compat.resourcefulBees$getApiaryOutput(tier);
            }
            return ItemStack.EMPTY;
        }));
    }


    public ItemStack getHiveOutput(BeehiveTier tier) {
        return hiveCombs().get(tier).create();
    }

    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return apiaryCombs().get(tier).create();
    }


    @Override
    public boolean matches(@NonNull BeeEntityRecipeInput recipeInput, @NonNull Level level) {
        return bees().contains(recipeInput.bee());
    }

    @Override
    public ItemStack assemble(@NonNull BeeEntityRecipeInput input) {
        return null;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<BeeEntityRecipeInput>> getSerializer() {
        return ModRecipeSerializers.HIVE_RECIPE.get();
    }

    @Override
    public RecipeType<? extends Recipe<BeeEntityRecipeInput>> getType() {
        return ModRecipes.HIVE_RECIPE_TYPE.get();
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
