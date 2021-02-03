
package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.ModRecipeSerializers;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RecipeUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipe implements IRecipe<IInventory> {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE_RECIPE_TYPE = IRecipeType.register(ResourcefulBees.MOD_ID + ":centrifuge");
    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final List<Pair<ItemStack, Float>> itemOutputs;
    public final List<Pair<FluidStack, Float>> fluidOutput;
    public final int time;
    public final int multiblockTime;
    public final boolean multiblock;
    public final boolean hasFluidOutput;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<Pair<ItemStack, Float>> itemOutputs, List<Pair<FluidStack, Float>> fluidOutput, int time, int multiblockTime, boolean multiblock, boolean hasFluidOutput) {
        this.id = id;
        this.ingredient = ingredient;
        this.itemOutputs = itemOutputs;
        this.fluidOutput = fluidOutput;
        this.time = time;
        this.multiblockTime = multiblockTime;
        this.multiblock = multiblock;
        this.hasFluidOutput = hasFluidOutput;
    }

    @Override
    public boolean matches(IInventory inventory, @Nonnull World world) {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack == ItemStack.EMPTY) return false;
        else {
            ItemStack[] matchingStacks = ingredient.getMatchingStacks();
            if (matchingStacks.length == 0) return stack.isEmpty();
            else {
                for (ItemStack itemstack : matchingStacks) {
                    if (itemstack.getItem() == stack.getItem()) {
                        if (itemstack.hasTag() && stack.hasTag()) {
                            return itemstack.getTag().equals(stack.getTag());
                        } else return !itemstack.hasTag() && !stack.hasTag();
                    }
                }
                return false;
            }
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    @Nonnull
    @Deprecated
    public ItemStack getCraftingResult(@Nonnull IInventory inventory) {
        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Deprecated
    @Override
    @Nonnull
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CENTRIFUGE_RECIPE.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return CENTRIFUGE_RECIPE_TYPE;
    }

    public static class Serializer<T extends CentrifugeRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T read(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "results");
            List<Pair<ItemStack, Float>> outputs = new ArrayList<>();
            List<Pair<FluidStack, Float>> fluidOutput = new ArrayList<>();

            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("item")) {
                    // collect data
                    String registryName = JSONUtils.getString(jsonObject, "item");
                    int count = JSONUtils.getInt(jsonObject, "count", 1);
                    Float chance = JSONUtils.getFloat(jsonObject, "chance", 1);

                    // collect nbt
                    CompoundNBT nbt = new CompoundNBT();
                    if (jsonObject.has("nbtData")) {
                        JsonElement nbtData = JSONUtils.getJsonObject(jsonObject, "nbtData");
                        nbt = CompoundNBT.CODEC.parse(JsonOps.INSTANCE, nbtData).resultOrPartial(e -> LOGGER.warn(String.format("Could not deserialize NBT: [%s]", nbtData.toString()))).get();
                    }

                    // create item stack
                    ItemStack stack = new ItemStack(BeeInfoUtils.getItem(registryName), count);

                    // apply nbt
                    if (nbt != null && !nbt.isEmpty()) stack.setTag(nbt);

                    // collect and set potion
                    if (registryName.equals("minecraft:potion") && jsonObject.has("potion")) {
                        stack.getOrCreateTag()
                                .putString("Potion", JSONUtils.getString(jsonObject, "potion"));
                    }
                    // add outputs
                    outputs.add(Pair.of(stack, chance));
                    fluidOutput.add(Pair.of(FluidStack.EMPTY, chance));
                } else if (jsonObject.has("fluid")) {
                    String fluid = JSONUtils.getString(jsonObject, "fluid");
                    int amount = JSONUtils.getInt(jsonObject, "amount", 1);
                    Float chance = JSONUtils.getFloat(jsonObject, "chance", 1);
                    FluidStack stack = new FluidStack(BeeInfoUtils.getFluid(fluid), amount);
                    outputs.add(Pair.of(ItemStack.EMPTY, 0f));
                    fluidOutput.add(Pair.of(stack, chance));
                }
            });

            int time = JSONUtils.getInt(json, "time", Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get());
            int multiblockTime = JSONUtils.getInt(json, "multiblockTime", time - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get());
            boolean multiblock = JSONUtils.getBoolean(json, "multiblock", false);
            boolean hasFluidOutput = JSONUtils.getBoolean(json, "hasFluidOutput", false);

            return this.factory.create(id, ingredient, outputs, fluidOutput, time, multiblockTime, multiblock, hasFluidOutput);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.read(buffer);
            List<Pair<ItemStack, Float>> itemOutputs = new ArrayList<>();
            List<Pair<FluidStack, Float>> fluidOutput = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> itemOutputs.add(Pair.of(RecipeUtils.readItemStack(buffer), buffer.readFloat())));
            IntStream.range(0, buffer.readInt()).forEach(value -> fluidOutput.add(Pair.of(FluidStack.readFromPacket(buffer), buffer.readFloat())));
            int time = buffer.readInt();
            int multiblockTime = buffer.readInt();
            boolean multiblock = buffer.readBoolean();
            boolean hasFluidOutput = buffer.readBoolean();
            return this.factory.create(id, ingredient, itemOutputs, fluidOutput, time, multiblockTime, multiblock, hasFluidOutput);
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeInt(recipe.itemOutputs.size());
            recipe.itemOutputs.forEach(itemStackFloatPair -> {
                ItemStack stack = itemStackFloatPair.getLeft();
                RecipeUtils.writeItemStack(stack, buffer);
                buffer.writeFloat(itemStackFloatPair.getRight());
            });
            buffer.writeInt(recipe.fluidOutput.size());
            recipe.fluidOutput.forEach(fluidStackFloatPair -> {
                FluidStack fluidStack = fluidStackFloatPair.getLeft();
                fluidStack.writeToPacket(buffer);
                buffer.writeFloat(fluidStackFloatPair.getRight());
            });
            buffer.writeInt(recipe.time);
            buffer.writeInt(recipe.multiblockTime);
            buffer.writeBoolean(recipe.multiblock);
            buffer.writeBoolean(recipe.hasFluidOutput);
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe> {
            T create(ResourceLocation id, Ingredient input, List<Pair<ItemStack, Float>> itemOutputs, List<Pair<FluidStack, Float>> fluidOutputs, int time, int multiblockTime, boolean multiblock, boolean hasFluidOutput);
        }
    }
}

