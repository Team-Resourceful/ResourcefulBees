package com.resourcefulbees.resourcefulbees.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class RecipeUtils {


    public static void writeItemStack(ItemStack stack, PacketBuffer buffer){
        if (stack.isEmpty()) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getIdFromItem(item));
            buffer.writeInt(stack.getCount());
            CompoundNBT compoundnbt = null;
            if (item.isDamageable() || item.shouldSyncTag()) {
                compoundnbt = stack.getShareTag();
            }

            buffer.writeCompoundTag(compoundnbt);
        }
    }

    public static ItemStack readItemStack(PacketBuffer buffer){
        if (!buffer.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            int itemId = buffer.readVarInt();
            int count = buffer.readInt();
            ItemStack itemstack = new ItemStack(Item.getItemById(itemId), count);
            itemstack.readShareTag(buffer.readCompoundTag());
            return itemstack;
        }
    }


}
