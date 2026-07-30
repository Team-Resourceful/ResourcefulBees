package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.MutationEntry;
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public record MutationRecipe(Color pollenBaseColor, Color pollenTopColor, Map<MutationType, WeightedCollection<MutationType>> mutations) implements Recipe<RecipeInput> {

    public static final MapCodec<MutationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Color.CODEC.fieldOf("pollenBaseColor").orElse(BeeConstants.DEFAULT_POLLEN_BASE_COLOR).forGetter(MutationRecipe::getPollenBaseColor),
        Color.CODEC.fieldOf("pollenTopColor").orElse(BeeConstants.DEFAULT_POLLEN_TOP_COLOR).forGetter(MutationRecipe::getPollenTopColor),
        MutationEntry.MUTATION_MAP_CODEC.fieldOf("mutations").forGetter(MutationRecipe::mutations)
    ).apply(instance, MutationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Map<MutationType, WeightedCollection<MutationType>>> MUTATION_MAP_STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NonNull Map<MutationType, WeightedCollection<MutationType>> decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();

            Map<MutationType, WeightedCollection<MutationType>> mutations = new HashMap<>(size);

            for (int index = 0; index < size; index++) {
                MutationEntry entry = MutationEntry.STREAM_CODEC.decode(buffer);

                mutations.put(entry.input(), entry.outputs());
            }

            return mutations;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, Map<MutationType, WeightedCollection<MutationType>> mutations) {
            buffer.writeVarInt(mutations.size());

            for (var entry : mutations.entrySet()) {
                MutationEntry.STREAM_CODEC.encode(
                        buffer,
                        new MutationEntry(entry.getKey(), entry.getValue())
                );
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, MutationRecipe> STREAM_CODEC = StreamCodec.composite(
            StreamCodecByteCodec.to(Color.BYTE_CODEC),
        MutationRecipe::getPollenBaseColor,
            StreamCodecByteCodec.to(Color.BYTE_CODEC),
        MutationRecipe::getPollenTopColor,
        MUTATION_MAP_STREAM_CODEC,
        MutationRecipe::mutations,
        MutationRecipe::new
    );

    public static RecipeHolder<MutationRecipe> getRecipe(@NotNull Level level, Identifier id) {
        return level.getServer().getRecipeManager().byKeyTyped(ModRecipes.MUTATION_RECIPE_TYPE.get(), ResourceKey.create(Registries.RECIPE, id));
    }

    public Color getPollenBaseColor() {
        return pollenBaseColor;
    }

    public Color getPollenTopColor() {
        return pollenTopColor;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return null;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipeSerializers.MUTATION_RECIPE.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.MUTATION_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public record Input() implements RecipeInput {








        @Override
        public ItemStack getItem(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }
    }
}
