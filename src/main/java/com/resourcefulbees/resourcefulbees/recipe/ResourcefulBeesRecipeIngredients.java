/*
package com.resourcefulbees.resourcefulbees.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;

import javax.annotation.Nonnull;

public class ResourcefulBeesRecipeIngredients implements IIngredientSerializer<ResourcefulBeesRecipeIngredients.CustomNBTIngredient> {
    public static final ResourcefulBeesRecipeIngredients INSTANCE = new ResourcefulBeesRecipeIngredients();

    //TODO Possibly remove this class due to it being redundant

    @Nonnull
    @Override
    public CustomNBTIngredient parse(PacketBuffer buffer) {
        String beeType = buffer.readString();
        CustomBeeData bee = BeeRegistry.getBeeData(beeType);
        if (bee !=null) {
            int count = Math.max(buffer.readInt(), 1);
            boolean block = buffer.readBoolean();

            ItemStack tmpStack = new ItemStack(block ? bee.getCombBlockItemRegistryObject().get() : bee.getCombRegistryObject().get(), count);

            return Ingredient.fromStacks(tmpStack);
        }
        else {
            throw new JsonSyntaxException("Invalid for bee: " + beeType);
        }
    }


    @Nonnull
    @Override
    public CustomNBTIngredient parse(@Nonnull JsonObject json) {
        String beeType = JSONUtils.getString(json, "beetype");
        if (BeeRegistry.getBeeData(beeType) != null) {
            CustomBeeData bee = BeeRegistry.getBeeData(beeType);
            int count = JSONUtils.getInt(json, "count", 1);
            boolean block = JSONUtils.getBoolean(json, "honeycombblock", false);

            ItemStack tmpStack = new ItemStack(block ? RegistryHandler.HONEYCOMB_BLOCK_ITEM.get() : RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), count);
            final CompoundNBT honeycombItemStackTag = tmpStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
            honeycombItemStackTag.putString(NBTConstants.NBT_COLOR, bee.ColorData.getHoneycombColor());
            honeycombItemStackTag.putString(NBTConstants.NBT_BEE_TYPE, beeType);

            return new CustomNBTIngredient(tmpStack);
        }
        else {
            throw new JsonSyntaxException("Invalid for bee: " + beeType);
        }
    }

    @Override
    public void write(PacketBuffer buffer, CustomNBTIngredient ingredient) {
            buffer.writeItemStack(ingredient.stack);
    }


    public static class CustomNBTIngredient extends NBTIngredient
    {
        public final ItemStack stack;
        public CustomNBTIngredient(ItemStack stack)
        {
            super(stack);
            this.stack = stack;
        }
    }
}*/
