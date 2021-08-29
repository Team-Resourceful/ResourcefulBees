
package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeFluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CentrifugeRecipe implements IRecipe<IInventory> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = IRecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final List<CentrifugeItemOutput> itemOutputs;
    private final List<CentrifugeFluidOutput> fluidOutputs;
    private final int time;
    private final int multiblockTime;
    private final boolean multiblock;

    public static final Codec<CentrifugeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::getIngredient),
            CentrifugeItemOutput.CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getItemOutputs),
            CentrifugeFluidOutput.CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getFluidOutputs),
            Codec.INT.fieldOf("time").orElse(CommonConfig.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()).forGetter(CentrifugeRecipe::getTime),
            Codec.INT.fieldOf("multiblockTime").orElse((CommonConfig.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()- CommonConfig.MULTIBLOCK_RECIPE_TIME_REDUCTION.get())*3).forGetter(CentrifugeRecipe::getMultiblockTime),
            Codec.BOOL.fieldOf("multiblock").orElse(false).forGetter(CentrifugeRecipe::isMultiblock)
    ).apply(instance, CentrifugeRecipe::new));

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<CentrifugeItemOutput> itemOutputs, List<CentrifugeFluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock) {
        this.id = id;
        this.ingredient = ingredient;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.time = time;
        this.multiblockTime = multiblockTime;
        this.multiblock = multiblock;
    }

    public CentrifugeRecipe(Ingredient ingredient, List<CentrifugeItemOutput> itemOutputs, List<CentrifugeFluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock) {
        this(null, ingredient, itemOutputs, fluidOutputs, time, multiblockTime, multiblock);
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

    public List<CentrifugeItemOutput> getItemOutputs() {
        return itemOutputs;
    }

    public List<CentrifugeFluidOutput> getFluidOutputs() {
        return fluidOutputs;
    }

    public int getTime() {
        return time;
    }

    public int getMultiblockTime() {
        return multiblockTime;
    }

    public boolean isMultiblock() {
        return multiblock;
    }

    //REQUIRED This needs serious testing to ensure it is working properly before pushing update!!!
    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRecipe> {
        final IRecipeFactory factory;

        public Serializer(Serializer.IRecipeFactory factory) {
            this.factory = factory;
        }

        @Override
        public @NotNull CentrifugeRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            CentrifugeRecipe recipe = CentrifugeRecipe.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Centrifuge Recipe!!"));
            return this.factory.create(id, recipe.ingredient, recipe.itemOutputs, recipe.fluidOutputs, recipe.time, recipe.multiblockTime, recipe.multiblock);
        }

        @Override
        public CentrifugeRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull PacketBuffer buffer) {
            try {
                return buffer.readWithCodec(CODEC);
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }

        //REQUIRED TEST THIS ON SERVERS!!!!!!!!!!!
        @Override
        public void toNetwork(@NotNull PacketBuffer buffer, @NotNull CentrifugeRecipe recipe) {
            try {
                buffer.writeWithCodec(CODEC, recipe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public interface IRecipeFactory {
            CentrifugeRecipe create(ResourceLocation id, Ingredient input, List<CentrifugeItemOutput> itemOutputs, List<CentrifugeFluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock);
        }
    }
}

