package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.nbt.CompoundNBT;

public class NBTHelper {

    public static CompoundNBT createHoneycombItemTag(String beeType) {
        CompoundNBT rootTag = new CompoundNBT();
        CompoundNBT childTag = new CompoundNBT();

        childTag.putString(NBTConstants.NBT_BEE_TYPE, beeType);
        childTag.putString(NBTConstants.NBT_COLOR, BeeRegistry.getInfo(beeType).getHoneycombColor());

        rootTag.put(NBTConstants.NBT_ROOT, childTag);

        return rootTag;
    }

    public static CompoundNBT createSpawnEggTag(String beeType) {
        CompoundNBT type = new CompoundNBT();
        CompoundNBT root = new CompoundNBT();
        type.putString(NBTConstants.NBT_BEE_TYPE, beeType);
        root.put(NBTConstants.NBT_ROOT, type);
        root.put(NBTConstants.NBT_SPAWN_EGG_DATA, type);
        return root;
    }
}
