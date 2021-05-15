
package com.teamresourceful.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.registry.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.utils.RecipeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipe implements Recipe<Container> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = RecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final List<ItemOutput> itemOutputs;
    private final List<FluidOutput> fluidOutputs;
    private final int time;
    private final int multiblockTime;
    private final boolean multiblock;

    public static final Codec<CentrifugeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(CentrifugeRecipe::getIngredient),
            ItemOutput.CODEC.listOf().fieldOf("itemOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getItemOutputs),
            FluidOutput.CODEC.listOf().fieldOf("fluidOutputs").orElse(new ArrayList<>()).forGetter(CentrifugeRecipe::getFluidOutputs),
            Codec.INT.fieldOf("time").orElse(Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()).forGetter(CentrifugeRecipe::getTime),
            Codec.INT.fieldOf("multiblockTime").orElse((Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get()-Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get())*3).forGetter(CentrifugeRecipe::getMultiblockTime),
            Codec.BOOL.fieldOf("multiblock").orElse(false).forGetter(CentrifugeRecipe::isMultiblock)
    ).apply(instance, CentrifugeRecipe::new));

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<ItemOutput> itemOutputs, List<FluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock) {
        this.id = id;
        this.ingredient = ingredient;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.time = time;
        this.multiblockTime = multiblockTime;
        this.multiblock = multiblock;
    }

    public CentrifugeRecipe(Ingredient ingredient, List<ItemOutput> itemOutputs, List<FluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock) {
        this(null, ingredient, itemOutputs, fluidOutputs, time, multiblockTime, multiblock);
    }

    @Override
    public boolean matches(Container inventory, @NotNull Level world) {
        ItemStack stack = inventory.getItem(0);
        if (stack == ItemStack.EMPTY) return false;
        else {
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length == 0) return false;
            else {
                return Arrays.stream(matchingStacks).anyMatch(itemStack -> AbstractContainerMenu.consideredTheSameItem(stack, itemStack));
            }
        }
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

    public List<ItemOutput> getItemOutputs() {
        return itemOutputs;
    }

    public List<FluidOutput> getFluidOutputs() {
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

    public static class Serializer<T extends CentrifugeRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public @NotNull T fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            CentrifugeRecipe recipe = CentrifugeRecipe.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Centrifuge Recipe!!"));
            return this.factory.create(id, recipe.ingredient, recipe.itemOutputs, recipe.fluidOutputs, recipe.time, recipe.multiblockTime, recipe.multiblock);
        }

        public T fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            List<ItemOutput> itemOutputs = new ArrayList<>();
            List<FluidOutput> fluidOutput = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> itemOutputs.add(new ItemOutput(RecipeUtils.readItemStack(buffer),0, buffer.readDouble())));
            IntStream.range(0, buffer.readInt()).forEach(value -> fluidOutput.add(new FluidOutput(FluidStack.readFromPacket(buffer),0, buffer.readDouble())));
            int time = buffer.readInt();
            int multiblockTime = buffer.readInt();
            boolean multiblock = buffer.readBoolean();
            return this.factory.create(id, ingredient, itemOutputs, fluidOutput, time, multiblockTime, multiblock);
        }

        public void toNetwork(@NotNull FriendlyByteBuf buffer, T recipe) {
            recipe.getIngredient().toNetwork(buffer);
            buffer.writeInt(recipe.getItemOutputs().size());
            recipe.getItemOutputs().forEach(itemOutput -> {
                ItemStack stack = itemOutput.getItemStack();
                RecipeUtils.writeItemStack(stack, buffer);
                buffer.writeDouble(itemOutput.getChance());
            });
            buffer.writeInt(recipe.getFluidOutputs().size());
            recipe.getFluidOutputs().forEach(fluidOutput -> {
                FluidStack fluidStack = fluidOutput.getFluidStack();
                fluidStack.writeToPacket(buffer);
                buffer.writeDouble(fluidOutput.getChance());
            });
            buffer.writeInt(recipe.getTime());
            buffer.writeInt(recipe.getMultiblockTime());
            buffer.writeBoolean(recipe.isMultiblock());
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe> {
            T create(ResourceLocation id, Ingredient input, List<ItemOutput> itemOutputs, List<FluidOutput> fluidOutputs, int time, int multiblockTime, boolean multiblock);
        }
    }
}

