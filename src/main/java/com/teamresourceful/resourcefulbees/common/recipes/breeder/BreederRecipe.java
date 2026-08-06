package com.teamresourceful.resourcefulbees.common.recipes.breeder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record BreederRecipe(
        ParentInput parent1,
        ParentInput parent2,
        Optional<Ingredient> optionalIngredient,
        WeightedCollection<ChildOutput> outputs,
        int time
) implements Recipe<BreederRecipe.Input> {

    public static final MapCodec<BreederRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ParentInput.CODEC.fieldOf("parent1").forGetter(BreederRecipe::parent1),
            ParentInput.CODEC.fieldOf("parent2").forGetter(BreederRecipe::parent2),
            Ingredient.CODEC.optionalFieldOf("optional").forGetter(BreederRecipe::optionalIngredient),
            CodecExtras.weightedCollection(ChildOutput.CODEC, ChildOutput::weight).fieldOf("outputs").forGetter(BreederRecipe::outputs),
            Codec.intRange(100, 72000).fieldOf("time").orElse(BreederConstants.DEFAULT_BREEDER_TIME).forGetter(BreederRecipe::time)
        ).apply(i, BreederRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BreederRecipe> STREAM_CODEC = StreamCodec.composite(
            ParentInput.STREAM_CODEC,
            BreederRecipe::parent1,

            ParentInput.STREAM_CODEC,
            BreederRecipe::parent2,

            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
            BreederRecipe::optionalIngredient,

            StreamCodecExtras.weightedCollection(ChildOutput.STREAM_CODEC, ChildOutput::weight),
            BreederRecipe::outputs,

            ByteBufCodecs.VAR_INT,
            BreederRecipe::time,

            BreederRecipe::new
    );

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return ModRecipeSerializers.BREEDER_RECIPE.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<Input>> getType() {
        return ModRecipes.BREEDER_RECIPE_TYPE.get();
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean matches(Input input, @NonNull Level level) {
        return parent1.matches(input.input1, input.feedItem1)
                && parent2.matches(input.input2, input.feedItem2)
                && (this.optionalIngredient.isPresent() && !this.optionalIngredient.get().isEmpty() && this.optionalIngredient.get().test(input.optionalInput));
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull Input input) {
        return outputs.next().child().create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    public record Input(ItemStack input1, ItemStack feedItem1, ItemStack input2, ItemStack feedItem2, ItemStack optionalInput) implements RecipeInput {

        @Override
        public @NonNull ItemStack getItem(int index) {
            return switch (index) {
                case 5 -> optionalInput;
                case 4 -> feedItem2;
                case 3 -> input2;
                case 2 -> feedItem1;
                default -> input1;
            };
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public boolean isEmpty() {
            return input1.isEmpty() || input2.isEmpty();
        }
    }

}
