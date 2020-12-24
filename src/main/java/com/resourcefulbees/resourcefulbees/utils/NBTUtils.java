package com.resourcefulbees.resourcefulbees.utils;

import net.minecraft.nbt.CompoundNBT;

public class NBTUtils {

    public static CompoundNBT writeBooleans(boolean[] booleans) {
        CompoundNBT list = new CompoundNBT();
        int i = 0;
        for (boolean bool : booleans) {
            list.putBoolean(String.valueOf(i), bool);
        }
        return list;
    }

    public static boolean[] loadBooleans(int quantity, CompoundNBT nbt) {
        boolean[] booleans = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            booleans[i] = nbt.getBoolean(String.valueOf(i));
        }
        return booleans;
    }

    public static int[] getFallbackIntArray(String value, CompoundNBT nbt, int fallbackSize) {
        int[] array = nbt.getIntArray(value);
        return array.length != fallbackSize ? new int[fallbackSize] : array;
    }
}
