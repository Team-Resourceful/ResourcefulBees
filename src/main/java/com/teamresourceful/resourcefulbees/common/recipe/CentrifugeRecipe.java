
package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CentrifugeRecipe implements IRecipe<IInventory> {

    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = IRecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");

    private final ResourceLocation id;
    private final int inputAmount;
    private final Ingredient ingredient;
    private final List<Output<ItemOutput>> itemOutputs;
    private final List<Output<FluidOutput>> fluidOutputs;
    private final int time;
    private final int energyPerTick;
    private final boolean conditioned;

    public static Codec<CentrifugeRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(CentrifugeRecipe::getId),
                CodecUtils.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::getIngredient),
                Output.ITEM_OUTPUT_CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getItemOutputs),
                Output.FLUID_OUTPUT_CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getFluidOutputs),
                Codec.INT.fieldOf("time").orElse(CommonConfig.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()).forGetter(CentrifugeRecipe::getTime),
                Codec.INT.fieldOf("energyPerTick").orElse(CommonConfig.RF_TICK_CENTRIFUGE.get()).forGetter(CentrifugeRecipe::getEnergyPerTick),
                CodecUtils.RECIPE_CONDITION_CODEC.fieldOf("conditions").orElse(true).forGetter(CentrifugeRecipe::matchesConditions)
        ).apply(instance, CentrifugeRecipe::new));
    }

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<Output<ItemOutput>> itemOutputs, List<Output<FluidOutput>> fluidOutputs, int time, int energyPerTick, boolean conditioned) {
        this.id = id;
        this.ingredient = ingredient;
        this.inputAmount = this.ingredient.getItems()[0].getCount();
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.time = time;
        this.energyPerTick = energyPerTick;
        this.conditioned = conditioned;
    }

    @Override
    public boolean matches(IInventory inventory, @NotNull World world) {
        ItemStack stack = inventory.getItem(0);
        if (stack == ItemStack.EMPTY) return false;
        else {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) return false;
            else {
                return Arrays.stream(matchingStacks).anyMatch(itemStack -> Container.consideredTheSameItem(stack, itemStack));
            }
        }
    }

    public boolean matchesConditions() {
        return this.conditioned;
    }

    public int getInputAmount() {
        return this.inputAmount;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull IInventory inventory) {
        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @NotNull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @NotNull
    @Override
    public IRecipeType<?> getType() {
        return CENTRIFUGE_RECIPE_TYPE;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public List<Output<ItemOutput>> getItemOutputs() {
        return itemOutputs;
    }

    public List<Output<FluidOutput>> getFluidOutputs() {
        return fluidOutputs;
    }

    public int getTime() {
        return time;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    //REQUIRED This needs serious testing to ensure it is working properly before pushing update!!!
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRecipe> {

        @Override
        public CentrifugeRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            CentrifugeRecipe recipe = CentrifugeRecipe.codec(id).parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Centrifuge Recipe!!"));
            return recipe.matchesConditions() ? recipe : null;
        }

        @Override
        public CentrifugeRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull PacketBuffer buffer) {
            try {
                return buffer.readWithCodec(CentrifugeRecipe.codec(id));
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }

        //REQUIRED TEST THIS ON SERVERS!!!!!!!!!!!
        @Override
        public void toNetwork(@NotNull PacketBuffer buffer, @NotNull CentrifugeRecipe recipe) {
            try {
                buffer.writeWithCodec(CentrifugeRecipe.codec(recipe.id), recipe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Output<T extends AbstractOutput> {
        public static final Codec<Output<ItemOutput>> ITEM_OUTPUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::getChance),
                ItemOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(Output::getPool)
        ).apply(instance, Output::new));

        public static final Codec<Output<FluidOutput>> FLUID_OUTPUT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Output::getChance),
                FluidOutput.RANDOM_COLLECTION_CODEC.fieldOf("pool").orElse(new RandomCollection<>()).forGetter(Output::getPool)
        ).apply(instance, Output::new));

        private final RandomCollection<T> pool;
        private final double chance;

        public Output(double chance, RandomCollection<T> pool) {
            this.chance = chance;
            this.pool = pool;
        }

        public RandomCollection<T> getPool() {
            return pool;
        }

        public double getChance() {
            return chance;
        }
    }
}

