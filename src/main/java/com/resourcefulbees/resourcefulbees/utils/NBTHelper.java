package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.nbt.CompoundNBT;

public class NBTHelper {

    public static CompoundNBT createHoneycombItemTag(String beeType, String color){
        CompoundNBT rootTag = new CompoundNBT();
        CompoundNBT childTag = new CompoundNBT();

        childTag.putString(NBTConstants.NBT_BEE_TYPE, beeType);
        childTag.putString(NBTConstants.NBT_COLOR, color);

        rootTag.put(NBTConstants.NBT_ROOT, childTag);

        return rootTag;
    }

    public static CompoundNBT createHoneycombItemTag(String beeType){
        CompoundNBT rootTag = new CompoundNBT();
        CompoundNBT childTag = new CompoundNBT();

        childTag.putString(NBTConstants.NBT_BEE_TYPE, beeType);
        childTag.putString(NBTConstants.NBT_COLOR, BeeInfo.getInfo(beeType).getHoneycombColor());

        rootTag.put(NBTConstants.NBT_ROOT, childTag);

        return rootTag;
    }
}
