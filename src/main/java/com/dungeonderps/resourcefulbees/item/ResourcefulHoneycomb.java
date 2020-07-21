package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class ResourcefulHoneycomb extends Item {

    public ResourcefulHoneycomb() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag(BeeConstants.NBT_ROOT);
        return honeycombNBT != null && !honeycombNBT.getString(BeeConstants.NBT_COLOR).isEmpty() ? Color.parseInt(honeycombNBT.getString(BeeConstants.NBT_COLOR)) : 0x000000;
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag(BeeConstants.NBT_ROOT);
        String name;
        if ((beeType != null && beeType.contains(BeeConstants.NBT_BEE_TYPE))) {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString(BeeConstants.NBT_BEE_TYPE) + "_honeycomb";
        } else {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb";
        }
        return name;
    }
}
