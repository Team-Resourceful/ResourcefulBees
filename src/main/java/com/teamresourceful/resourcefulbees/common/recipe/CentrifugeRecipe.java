
package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CentrifugeRecipe implements Recipe<Container> {

    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = RecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");

    private final ResourceLocation id;
    private final int inputAmount;
    private final Ingredient ingredient;
    private final List<Output<ItemOutput>> itemOutputs;
    private final List<Output<FluidOutput>> fluidOutputs;
    private final int time;
    private final int energyPerTick;

    public static Codec<CentrifugeRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(CentrifugeRecipe::getId),
                CodecUtils.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::getIngredient),
                Output.ITEM_OUTPUT_CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getItemOutputs),
                Output.FLUID_OUTPUT_CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getFluidOutputs),
                Codec.INT.fieldOf("time").orElse(CommonConfig.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()).forGetter(CentrifugeRecipe::getTime),
                Codec.INT.fieldOf("energyPerTick").orElse(CommonConfig.RF_TICK_CENTRIFUGE.get()).forGetter(CentrifugeRecipe::getEnergyPerTick)
        ).apply(instance, CentrifugeRecipe::new));
    }

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<Output<ItemOutput>> itemOutputs, List<Output<FluidOutput>> fluidOutputs, int time, int energyPerTick) {
        this.id = id;
        this.ingredient = ingredient;
        this.inputAmount = this.ingredient.getItems()[0].getCount();
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.time = time;
        this.energyPerTick = energyPerTick;
    }

    @Override
    public boolean matches(Container inventory, @NotNull Level world) {
        ItemStack stack = inventory.getItem(0);
        if (stack == ItemStack.EMPTY) return false;
        else {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) return false;
            else {
                return Arrays.stream(matchingStacks).anyMatch(itemStack -> ItemStack.isSameItemSameTags(stack, itemStack));
            }
        }
    }

    public int getInputAmount() {
        return this.inputAmount;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    @Override
    public @NotNull ItemStack assemble(@NotNull Container inventory) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
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
    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CentrifugeRecipe> {

        @Override
        public @NotNull CentrifugeRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return CentrifugeRecipe.codec(id).parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Centrifuge Recipe!!"));
        }

        /**
         * THE REASON
         * <br>-----------------------------<br>
         * So if we used buffer.writeWithCodec we would exceed buffer max capacity and would error the client and won't allow
         * the player to join.
         * Writing a string with normal json also causes an error with buffer max capacity.
         * From our testing Compressed json allows for a lot of data, (test case: tested with a pool of 1050 elements)
         * and worked fine.
         * Please don't be stupid and test as much as possible if trying to change this in the future.
         */

        @Override
        public CentrifugeRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            Optional<CentrifugeRecipe> result = CentrifugeRecipe.codec(id).parse(JsonOps.COMPRESSED, ModConstants.GSON.fromJson(buffer.readUtf(), JsonArray.class)).result();
            return result.orElse(null);

        }

        //REQUIRED TEST THIS ON SERVERS!!!!!!!!!!!
        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CentrifugeRecipe recipe) {
            CentrifugeRecipe.codec(recipe.id).encodeStart(JsonOps.COMPRESSED, recipe).result().ifPresent(element -> buffer.writeUtf(element.toString()));
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

