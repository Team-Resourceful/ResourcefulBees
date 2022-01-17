
package com.teamresourceful.resourcefulbees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.ingredients.IAmountSensitive;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<Output<ItemOutput>> itemOutputs, List<Output<FluidOutput>> fluidOutputs, int time, int energyPerTick) implements CodecRecipe<Container> {

    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = RecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");

    public static Codec<CentrifugeRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(CentrifugeRecipe::id),
                CodecUtils.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::ingredient),
                Output.ITEM_OUTPUT_CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::itemOutputs),
                Output.FLUID_OUTPUT_CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::fluidOutputs),
                Codec.INT.fieldOf("time").orElse(CommonConfig.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()).forGetter(CentrifugeRecipe::time),
                Codec.INT.fieldOf("energyPerTick").orElse(CommonConfig.RF_TICK_CENTRIFUGE.get()).forGetter(CentrifugeRecipe::energyPerTick)
        ).apply(instance, CentrifugeRecipe::new));
    }

    @Override
    public boolean matches(Container inventory, @NotNull Level world) {
        ItemStack stack = inventory.getItem(0);
        return !stack.isEmpty() && ingredient.test(stack);
    }

    public int getInputAmount() {
        return ingredient instanceof IAmountSensitive amountSensitive ? amountSensitive.getAmount() : 1;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return CENTRIFUGE_RECIPE_TYPE;
    }

    public record Output<T extends AbstractOutput>(double chance, RandomCollection<T> pool) {
        public static final Codec<Output<ItemOutput>> ITEM_OUTPUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::chance),
                ItemOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(Output::pool)
        ).apply(instance, Output::new));

        public static final Codec<Output<FluidOutput>> FLUID_OUTPUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::chance),
                FluidOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(Output::pool)
        ).apply(instance, Output::new));
    }
}

