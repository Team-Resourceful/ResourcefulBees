package com.dungeonderps.resourcefulbees.utils;

import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import net.minecraft.nbt.CompoundNBT;

public class NBTHelper {

    public static CompoundNBT createHoneycombItemTag(String beeType, String color){
        CompoundNBT rootTag = new CompoundNBT();
        CompoundNBT childTag = new CompoundNBT();

        childTag.putString(BeeConstants.NBT_BEE_TYPE, beeType);
        childTag.putString(BeeConstants.NBT_COLOR, color);

        rootTag.put(BeeConstants.NBT_ROOT, childTag);

        return rootTag;
    }


}
