package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
}
