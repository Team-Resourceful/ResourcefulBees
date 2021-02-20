package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.block.HoneyTank;
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
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        List<ItemStack> stacks = getTanks(inventory);
        CompoundNBT tag = new CompoundNBT();
        HoneyTankTileEntity.TankTier tier = HoneyTankTileEntity.TankTier.getTier(result.getItem());
        for (ItemStack stack : stacks) {
            if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("fluid")) {
                continue;
            }
            CompoundNBT fluid = stack.getTag().getCompound("fluid");
            if (tag.contains("fluid")) {
                FluidTank mainStack = new FluidTank(tier.getMaxFillAmount(), honeyFluidPredicate());
                mainStack.readFromNBT(tag.getCompound("fluid"));
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(fluid);
                if (mainStack.getFluid().containsFluid(fluidStack)) {
                    mainStack.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    tag.put("fluid", mainStack.writeToNBT(new CompoundNBT()));
                }
            } else {
                tag.put("fluid", fluid);
            }
        }
        if (tag.isEmpty()) {
            return this.result;
        } else {
            tag.putInt("tier", tier.getTier());
            ItemStack result = this.result.copy();
            result.setTag(tag);
            return result;
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
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.HONEY_TANK_RECIPE.get();
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        @Override
        public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe recipe = super.read(recipeId, json);
            return new HoneyTankRecipe(recipeId, recipe.getIngredients(), recipe.getRecipeOutput());
        }

        @Override
        public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe recipe = super.read(recipeId, buffer);
            return new HoneyTankRecipe(recipeId, recipe.getIngredients(), recipe.getRecipeOutput());
        }
    }
}
