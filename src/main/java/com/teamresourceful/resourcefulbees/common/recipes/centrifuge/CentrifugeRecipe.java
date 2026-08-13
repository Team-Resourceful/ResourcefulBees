
package com.teamresourceful.resourcefulbees.common.recipes.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CentrifugeConfig;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.lib.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//todo add boolean `manualApproved` to dis/allow recipe to be used in manual centrifuge
// and add a powered centrifuge block which is an upgraded form of the manual centrifuge
public record CentrifugeRecipe(
        Ingredient ingredient,
        int inputAmount,
        List<Output<ItemOutput, ItemStack>> itemOutputs,
        List<Output<FluidOutput, FluidStack>> fluidOutputs,
        int time,
        int energyPerTick,
        Optional<Integer> rotations
) implements Recipe<CentrifugeRecipe.Input> {

    public static final MapCodec<CentrifugeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::ingredient),
            Codec.INT.fieldOf("inputAmount").orElse(1).forGetter(CentrifugeRecipe::inputAmount),
            Output.codec(ItemOutput.CODEC).listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::itemOutputs),
            Output.codec(FluidOutput.CODEC).listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::fluidOutputs),
            Codec.INT.fieldOf("time").orElse(CentrifugeConfig.defaultCentrifugeRecipeTime).forGetter(CentrifugeRecipe::time),
            Codec.INT.fieldOf("energyPerTick").orElse(CentrifugeConfig.centrifugeRfPerTick).forGetter(CentrifugeRecipe::energyPerTick),
            Codec.INT.optionalFieldOf("rotations").forGetter(CentrifugeRecipe::rotations)
    ).apply(instance, CentrifugeRecipe::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec
            .composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    CentrifugeRecipe::ingredient,

                    ByteBufCodecs.VAR_INT,
                    CentrifugeRecipe::inputAmount,

                    Output.streamCodec(ItemOutput.STREAM_CODEC).apply(ByteBufCodecs.list()),
                    CentrifugeRecipe::itemOutputs,

                    Output.streamCodec(FluidOutput.STREAM_CODEC).apply(ByteBufCodecs.list()),
                    CentrifugeRecipe::fluidOutputs,

                    ByteBufCodecs.VAR_INT,
                    CentrifugeRecipe::time,

                    ByteBufCodecs.VAR_INT,
                    CentrifugeRecipe::energyPerTick,

                    ByteBufCodecs.optional(ByteBufCodecs.VAR_INT),
                    CentrifugeRecipe::rotations,

                    CentrifugeRecipe::new
            );

    public static Optional<RecipeHolder<CentrifugeRecipe>> getRecipe(Level level, ItemStack recipeStack) {
        return level.getServer() != null
                ? level.getServer().getRecipeManager().getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), new Input(recipeStack), level)
                : Optional.empty();
    }

    @Override
    public boolean matches(Input input, @NonNull Level level) {
        return ingredient.test(input.input()) && inputAmount == input.input().count();
    }

    @Override
    public ItemStack assemble(@NonNull Input input) {
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
    public @NonNull RecipeSerializer<? extends Recipe<Input>> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<Input>> getType() {
        return ModRecipes.CENTRIFUGE_RECIPE_TYPE.get();
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public int getRotations() {
        return rotations().orElse(((time / 20)/8) * 2);
    }

    public record Output<T extends AbstractOutput<E>, E>(double chance, WeightedCollection<T> pool) {

        public static <A extends AbstractOutput<B>, B> Codec<Output<A, B>> codec(Codec<A> codec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::chance),
                    CodecExtras.weightedCollection(codec, AbstractOutput::weight).fieldOf("pool").orElse(new WeightedCollection<>()).forGetter(Output::pool)
            ).apply(instance, Output::new));
        }

        public static <A extends AbstractOutput<B>, B> StreamCodec<RegistryFriendlyByteBuf, Output<A, B>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, A> codec) {
            return StreamCodec.composite(
                    ByteBufCodecs.DOUBLE,
                    Output::chance,
                    StreamCodecExtras.weightedCollection(codec, AbstractOutput::weight),
                    Output::pool,
                    Output::new
            );
        }

        public T getRandomResult() {
            return pool().next();
        }
    }

    public record Input(ItemStack input) implements RecipeInput {

        @Override
        public @NonNull ItemStack getItem(int index) {
            return input;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return input.isEmpty();
        }
    }
}

