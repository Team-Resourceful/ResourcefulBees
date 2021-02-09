package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.resourcefulbees.resourcefulbees.registry.ModRecipeSerializers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class HoneyTankRecipe extends ShapedRecipe {

    public final ItemStack result;

    public HoneyTankRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, "", 3, 3, ingredients, result);
        this.result = result;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inventory) {
        ItemStack toCopy = inventory.getStackInSlot(4);
        if (!toCopy.hasTag() || toCopy.getTag() == null || toCopy.getTag().isEmpty() || !toCopy.getTag().contains("fluid")) {
            return this.result;
        } else {
            ItemStack result = this.result;
            INBT fluid = toCopy.getTag().get("fluid");
            int tier = HoneyTankTileEntity.TankTier.getTier(result.getItem()).getTier();
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("fluid", fluid);
            nbt.putInt("tier", tier);
            result.setTag(nbt);
            return result;
        }
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
