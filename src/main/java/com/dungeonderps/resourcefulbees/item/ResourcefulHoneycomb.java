package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ResourcefulHoneycomb extends Item {

    public ResourcefulHoneycomb() {
        super(new Properties().group(ItemGroup.MATERIALS));
    }

    //TODO Consider using two separate textures for honeycomb
    // -One texture for outline
    // -Another texture to be colored.
    public static int getColor(ItemStack stack, int tintIndex){

        CompoundNBT honeycombNBT = stack.getChildTag(BeeConst.NBT_ROOT);
        return honeycombNBT != null && !honeycombNBT.getString(BeeConst.NBT_COLOR).isEmpty() ? Color.parseInt(honeycombNBT.getString(BeeConst.NBT_COLOR)) : 0x000000;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT beeType = stack.getChildTag(BeeConst.NBT_ROOT);
        String name;
        if ((beeType != null && beeType.contains(BeeConst.NBT_BEE_TYPE))) {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + beeType.getString(BeeConst.NBT_BEE_TYPE).toLowerCase() + "_honeycomb";
        } else {
            name = "item" + '.' + ResourcefulBees.MOD_ID + '.' + "resourceful_honeycomb";
        }
        return name;
    }
}
