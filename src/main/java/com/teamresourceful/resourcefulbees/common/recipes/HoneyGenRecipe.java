package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record HoneyGenRecipe(FluidIngredient honey, int energyFillRate, int honeyDrainRate) implements Recipe<HoneyGenRecipe.Input> {

    public static MapCodec<HoneyGenRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FluidIngredient.CODEC.fieldOf("honey").forGetter(HoneyGenRecipe::honey),
            CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("energyFillRate", 125).forGetter(HoneyGenRecipe::energyFillRate),
            CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("honeyDrainRate", 5).forGetter(HoneyGenRecipe::honeyDrainRate)
        ).apply(instance, HoneyGenRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HoneyGenRecipe> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC,
            HoneyGenRecipe::honey,

            ByteBufCodecs.VAR_INT,
            HoneyGenRecipe::energyFillRate,

            ByteBufCodecs.VAR_INT,
            HoneyGenRecipe::honeyDrainRate,

            HoneyGenRecipe::new
    );

    public static Optional<RecipeHolder<HoneyGenRecipe>> findRecipe(RecipeManager manager, FluidStack fluid, Level level) {
        return manager.getRecipeFor(ModRecipes.HONEY_GEN_RECIPE_TYPE.get(), new Input(fluid), level);
    }

    public static boolean matches(RecipeManager manager, FluidStack fluid, Level level) {
        return findRecipe(manager, fluid, level).isPresent();
    }

    @Override
    public boolean matches(@NonNull Input input, @NonNull Level level) {
        return honey.test(input.fluid);
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull Input input) {
        return ItemStack.EMPTY;
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
        return ModRecipeSerializers.HONEY_GEN_RECIPE.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<Input>> getType() {
        return ModRecipes.HONEY_GEN_RECIPE_TYPE.get();
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }


    public record Input(FluidStack fluid) implements RecipeInput {

        @Override
        public @NonNull ItemStack getItem(int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
