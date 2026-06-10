
package com.teamresourceful.resourcefulbees.common.recipes.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CentrifugeConfig;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeMatcher;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CentrifugeRecipe(
        ResourceLocation id,
        Ingredient ingredient,
        int inputAmount,
        List<Output<ItemOutput, ItemStack>> itemOutputs,
        List<Output<FluidOutput, RecipeFluid>> fluidOutputs,
        int time,
        int energyPerTick,
        Optional<Integer> rotations
) implements CodecRecipe<RecipeInput>, RecipeMatcher {

    public static Codec<CentrifugeRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::ingredient),
                Codec.INT.fieldOf("inputAmount").orElse(1).forGetter(CentrifugeRecipe::inputAmount),
                Output.codec(ItemOutput.CODEC).listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::itemOutputs),
                Output.codec(FluidOutput.CODEC).listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::fluidOutputs),
                Codec.INT.fieldOf("time").orElse(CentrifugeConfig.defaultCentrifugeRecipeTime).forGetter(CentrifugeRecipe::time),
                Codec.INT.fieldOf("energyPerTick").orElse(CentrifugeConfig.centrifugeRfPerTick).forGetter(CentrifugeRecipe::energyPerTick),
                Codec.INT.optionalFieldOf("rotations").forGetter(CentrifugeRecipe::rotations)
        ).apply(instance, CentrifugeRecipe::new));
    }

    /*@Override
    public boolean matches(Container inventory, @NotNull Level world) {
        ItemStack stack = inventory.getItem(0);
        return !stack.isEmpty() && ingredient.test(stack);
    }*/

    @Override
    public boolean matches(RecipeInput inventory) {
        ItemStack stack = inventory.getItem(0);
        return !stack.isEmpty() && ItemStack.isSameItemSameComponents(ingredient.test(stack), stack);
    }

    public static Optional<RecipeHolder<CentrifugeRecipe>> getRecipe(Level level, ItemStack recipeStack) {
        return level != null
                ? level.getServer().getRecipeManager().getRecipeFor(ModRecipes.CENTRIFUGE_RECIPE_TYPE.get(), CraftingInput.of(1,1,List.of(recipeStack)), level)
                : Optional.empty();
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public CodecRecipeSerializer<? extends CodecRecipe<RecipeInput>> serializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CENTRIFUGE_RECIPE_TYPE.get();
    }

    public int getRotations() {
        return rotations().orElse(((time / 20)/8) * 2);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public @NotNull String toString() {
        return "";
    }

    public record Output<T extends AbstractOutput<E>, E>(double chance, WeightedCollection<T> pool) {

        public static <A extends AbstractOutput<B>, B> Codec<Output<A, B>> codec(Codec<A> codec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::chance),
                    CodecExtras.weightedCollection(codec, AbstractOutput::weight).fieldOf("pool").orElse(new WeightedCollection<>()).forGetter(Output::pool)
            ).apply(instance, Output::new));
        }

        public T getRandomResult() {
            return pool().next();
        }
    }
}

