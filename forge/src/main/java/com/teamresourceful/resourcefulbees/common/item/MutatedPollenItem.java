package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class MutatedPollenItem extends Item {
    public MutatedPollenItem(Properties arg) {
        super(arg);
        Color color = ConstantColors.aliceblue;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ((MutatedPollenItem) stack.getItem()).getBasePollenColor(stack) : ((MutatedPollenItem) stack.getItem()).getTopPollenColor(stack);
    }

    // default color: 13408304, 12681264
    private int getTopPollenColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(NBTConstants.POLLEN_TOP_COLOR) ? Color.parseColor(tag.getString(NBTConstants.POLLEN_TOP_COLOR)) : BeeConstants.DEFAULT_POLLEN_TOP_INT_COLOR;
    }

    private int getBasePollenColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(NBTConstants.POLLEN_BASE_COLOR) ? Color.parseColor(tag.getString(NBTConstants.POLLEN_BASE_COLOR)) : BeeConstants.DEFAULT_POLLEN_TOP_INT_COLOR;
    }
}
