package com.teamresourceful.resourcefulbees.utils;

import com.teamresourceful.resourcefulbees.lib.ModConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NBTUtils {

    private NBTUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static Tag writeBooleans(boolean[] booleans) {
        CompoundTag list = new CompoundTag();
        int i = 0;
        for (boolean bool : booleans) {
            list.putBoolean(String.valueOf(i), bool);
        }
        return list;
    }

    public static boolean[] loadBooleans(int quantity, CompoundTag nbt) {
        boolean[] booleans = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            booleans[i] = nbt.getBoolean(String.valueOf(i));
        }
        return booleans;
    }

    public static int[] getFallbackIntArray(String value, CompoundTag nbt, int fallbackSize) {
        int[] array = nbt.getIntArray(value);
        return array.length != fallbackSize ? new int[fallbackSize] : array;
    }
}
