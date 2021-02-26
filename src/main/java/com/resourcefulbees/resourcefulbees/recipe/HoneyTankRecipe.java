package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.block.HoneyTank;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModRecipeSerializers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HoneyTankRecipe extends ShapedRecipe {

    public final ItemStack result;

    public HoneyTankRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, "", 3, 3, ingredients, result);
        this.result = result;
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull CraftingInventory inventory) {
        List<ItemStack> stacks = getTanks(inventory);
        CompoundNBT tag = new CompoundNBT();
        HoneyTankTileEntity.TankTier tier = HoneyTankTileEntity.TankTier.getTier(result.getItem());
        for (ItemStack stack : stacks) {
            if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains(NBTConstants.NBT_FLUID)) {
                continue;
            }
            CompoundNBT fluid = stack.getTag().getCompound(NBTConstants.NBT_FLUID);
            if (tag.contains(NBTConstants.NBT_FLUID)) {
                FluidTank mainStack = new FluidTank(tier.getMaxFillAmount(), honeyFluidPredicate());
                mainStack.readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluid);
                if (mainStack.getFluid().containsFluid(fluidStack)) {
                    mainStack.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    tag.put(NBTConstants.NBT_FLUID, mainStack.writeToNBT(new CompoundNBT()));
                }
            } else {
                tag.put(NBTConstants.NBT_FLUID, fluid);
            }
        }
        if (tag.isEmpty()) {
            return this.result;
        } else {
            tag.putInt("tier", tier.getTier());
            ItemStack resultCopy = this.result.copy();
            resultCopy.setTag(tag);
            return resultCopy;
        }
    }

    protected static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().isIn(BeeInfoUtils.getFluidTag("forge:honey"));
    }

    public List<ItemStack> getTanks(CraftingInventory inventory) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (Block.getBlockFromItem(item.getItem()) instanceof HoneyTank) {
                stacks.add(item);
            }
        }
        return stacks;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HONEY_TANK_RECIPE.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        @Override
        public @NotNull ShapedRecipe read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapedRecipe recipe = super.read(recipeId, json);
            return new HoneyTankRecipe(recipeId, recipe.getIngredients(), recipe.getRecipeOutput());
        }

        @Override
        public ShapedRecipe read(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            ShapedRecipe recipe = super.read(recipeId, buffer);
            assert recipe != null;
            return new HoneyTankRecipe(recipeId, recipe.getIngredients(), recipe.getRecipeOutput());
        }
    }
}
