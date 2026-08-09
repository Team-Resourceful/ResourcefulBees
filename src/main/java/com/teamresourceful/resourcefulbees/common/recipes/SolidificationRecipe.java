package com.teamresourceful.resourcefulbees.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record SolidificationRecipe(
        SizedFluidIngredient fluid,
        ItemStackTemplate stack,
        int time
) implements Recipe<SolidificationRecipe.Input> {

    public static MapCodec<SolidificationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedFluidIngredient.CODEC.fieldOf("fluid").forGetter(SolidificationRecipe::fluid),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(SolidificationRecipe::stack),
            Codec.INT.fieldOf("time").orElse(200).forGetter(SolidificationRecipe::time)
    ).apply(instance, SolidificationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SolidificationRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedFluidIngredient.STREAM_CODEC,
            SolidificationRecipe::fluid,

            ItemStackTemplate.STREAM_CODEC,
            SolidificationRecipe::stack,

            ByteBufCodecs.VAR_INT,
            SolidificationRecipe::time,

            SolidificationRecipe::new
    );

    public static Optional<RecipeHolder<SolidificationRecipe>> findRecipe(RecipeManager manager, FluidStack fluid, Level level) {
        return manager.getRecipeFor(ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get(), new Input(fluid), level);
    }

    public static boolean matches(RecipeManager manager, FluidStack fluid, Level level) {
        return findRecipe(manager, fluid, level).isPresent();
    }

    @Override
    public boolean matches(@NonNull Input input, @NonNull Level level) {
        return fluid.test(input.fluid);
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
        return ModRecipeSerializers.SOLIDIFICATION_RECIPE.get();
    }

    @Override
    public @NonNull RecipeType<? extends Recipe<Input>> getType() {
        return ModRecipes.SOLIDIFICATION_RECIPE_TYPE.get();
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
