
package com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CentrifugeConfig;
import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CentrifugeRecipe(
        ResourceLocation id,
        Ingredient ingredient,
        int inputAmount,
        List<Output<ItemOutput, ItemStack>> itemOutputs,
        List<Output<FluidOutput, FluidStack>> fluidOutputs,
        int time,
        int energyPerTick,
        Optional<Integer> rotations
) implements CodecRecipe<Container>, FilterSlot.IFilterMatch {

    public static Codec<CentrifugeRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                IngredientCodec.CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::ingredient),
                Codec.INT.fieldOf("inputAmount").orElse(1).forGetter(CentrifugeRecipe::inputAmount),
                Output.codec(ItemOutput.CODEC).listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::itemOutputs),
                Output.codec(FluidOutput.CODEC).listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::fluidOutputs),
                Codec.INT.fieldOf("time").orElse(CentrifugeConfig.defaultCentrifugeRecipeTime).forGetter(CentrifugeRecipe::time),
                Codec.INT.fieldOf("energyPerTick").orElse(CentrifugeConfig.centrifugeRfPerTick).forGetter(CentrifugeRecipe::energyPerTick),
                Codec.INT.optionalFieldOf("rotations").forGetter(CentrifugeRecipe::rotations)
        ).apply(instance, CentrifugeRecipe::new));
    }

    @Override
    public boolean matches(Container inventory, @NotNull Level world) {
        ItemStack stack = inventory.getItem(0);
        return !stack.isEmpty() && ingredient.test(stack);
    }

    public boolean matchesItemAndNBT(Container inventory) {
        ItemStack stack = inventory.getItem(0);
        return !stack.isEmpty() && ItemStack.isSameItemSameTags(ingredient.getItems()[0], stack);
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get();
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

        public T getRandomResult() {
            return pool().next();
        }
    }
}

