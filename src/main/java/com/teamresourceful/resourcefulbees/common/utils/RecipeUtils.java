package com.teamresourceful.resourcefulbees.common.utils;

import com.google.gson.JsonElement;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class RecipeUtils {

    private RecipeUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void writeItemStack(ItemStack stack, PacketBuffer buffer){
        if (stack.isEmpty()) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            Item item = stack.getItem();
            buffer.writeVarInt(Item.getId(item));
            buffer.writeInt(stack.getCount());
            CompoundNBT compoundnbt = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                compoundnbt = stack.getShareTag();
            }

            buffer.writeNbt(compoundnbt);
        }
    }

    public static ItemStack readItemStack(PacketBuffer buffer){
        if (!buffer.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            int itemId = buffer.readVarInt();
            int count = buffer.readInt();
            ItemStack itemstack = new ItemStack(Item.byId(itemId), count);
            itemstack.readShareTag(buffer.readNbt());
            return itemstack;
        }
    }

    public static int getIngredientCount(Ingredient ingredient){
        JsonElement count = ingredient.toJson().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
        return count != null && !count.isJsonNull() ? count.getAsInt() : 1;
    }
}
