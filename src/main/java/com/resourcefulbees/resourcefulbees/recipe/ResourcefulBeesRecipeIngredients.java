package com.resourcefulbees.resourcefulbees.recipe;

import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;

public class ResourcefulBeesRecipeIngredients implements IIngredientSerializer<ResourcefulBeesRecipeIngredients.CustomNBTIngredient> {
    public static final ResourcefulBeesRecipeIngredients INSTANCE = new ResourcefulBeesRecipeIngredients();

    @Override
    public CustomNBTIngredient parse(PacketBuffer buffer) {
        String beeType = buffer.readString();
        BeeData bee = BeeInfo.getInfo(beeType);
        if (bee !=null) {
            int count = Math.max(buffer.readInt(), 1);
            Boolean block = buffer.readBoolean();

            ItemStack tmpStack = new ItemStack(block ? RegistryHandler.HONEYCOMB_BLOCK_ITEM.get() : RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), count);
            final CompoundNBT honeycombItemStackTag = tmpStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
            honeycombItemStackTag.putString(NBTConstants.NBT_COLOR, bee.getHoneycombColor());
            honeycombItemStackTag.putString(NBTConstants.NBT_BEE_TYPE, beeType);

            return new CustomNBTIngredient(tmpStack);
        }
        else {
            throw new JsonSyntaxException("Invalid for bee: " + beeType);
        }
    }

    @Override
    public CustomNBTIngredient parse(JsonObject json) {
        String beeType = JSONUtils.getString(json, "beetype");
        if (BeeInfo.BEE_INFO.get(beeType) !=null) {
            BeeData bee = BeeInfo.getInfo(beeType);
            int count = JSONUtils.getInt(json, "count", 1);
            boolean block = JSONUtils.getBoolean(json, "honeycombblock", false);

            ItemStack tmpStack = new ItemStack(block ? RegistryHandler.HONEYCOMB_BLOCK_ITEM.get() : RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), count);
            final CompoundNBT honeycombItemStackTag = tmpStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
            honeycombItemStackTag.putString(NBTConstants.NBT_COLOR, bee.getHoneycombColor());
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
}