package com.teamresourceful.resourcefulbees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SolidificationRecipe implements IRecipe<IInventory> {

    public static final IRecipeType<CentrifugeRecipe> SOLIDIFICATION_RECIPE_TYPE = IRecipeType.register(ResourcefulBees.MOD_ID + ":solidification");

    private final ResourceLocation id;
    private final FluidStack fluid;
    private final ItemStack stack;

    public static Codec<SolidificationRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(SolidificationRecipe::getId),
                CodecUtils.FLUID_STACK_CODEC.fieldOf("fluid").forGetter(SolidificationRecipe::getFluid),
                CodecUtils.ITEM_STACK_CODEC.fieldOf("result").forGetter(SolidificationRecipe::getStack)
        ).apply(instance, SolidificationRecipe::new));
    }

    public SolidificationRecipe(ResourceLocation id, FluidStack fluid, ItemStack stack) {
        this.id = id;
        this.fluid = fluid;
        this.stack = stack;
    }

    public static Optional<SolidificationRecipe> findRecipe(RecipeManager manager, FluidStack fluid) {
        return ((RecipeManagerAccessorInvoker)manager).callByType(SOLIDIFICATION_RECIPE_TYPE).values()
                .stream().filter(SolidificationRecipe.class::isInstance)
                .map(SolidificationRecipe.class::cast)
                .filter(recipe -> recipe.getFluid().isFluidEqual(fluid)).findFirst();
    }

    public static boolean matches(RecipeManager manager, FluidStack fluid) {
        return findRecipe(manager, fluid).isPresent();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(@NotNull IInventory inventory, @NotNull World world) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull IInventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOLIDIFICATION_RECIPE.get();
    }

    @Override
    public @NotNull IRecipeType<?> getType() {
        return SOLIDIFICATION_RECIPE_TYPE;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SolidificationRecipe> {

        @Override
        public @NotNull SolidificationRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return SolidificationRecipe.codec(id).parse(JsonOps.INSTANCE, json).getOrThrow(false, s -> ResourcefulBees.LOGGER.error("Could not parse Solidification Recipe!!"));
        }

        @Nullable
        @Override
        public SolidificationRecipe fromNetwork(@NotNull ResourceLocation id, PacketBuffer buffer) {
            Optional<SolidificationRecipe> result = SolidificationRecipe.codec(id).parse(JsonOps.COMPRESSED, ModConstants.GSON.fromJson(buffer.readUtf(), JsonArray.class)).result();
            return result.orElse(null);
        }

        @Override
        public void toNetwork(@NotNull PacketBuffer buffer, SolidificationRecipe recipe) {
            SolidificationRecipe.codec(recipe.id).encodeStart(JsonOps.COMPRESSED, recipe).result().ifPresent(element -> buffer.writeUtf(element.toString()));
        }
    }
}
