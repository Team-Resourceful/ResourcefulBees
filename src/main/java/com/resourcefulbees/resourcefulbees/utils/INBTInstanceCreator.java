package com.resourcefulbees.resourcefulbees.utils;

import com.google.gson.InstanceCreator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

import java.lang.reflect.Type;

public class INBTInstanceCreator implements InstanceCreator<INBT> {
    @Override
    public INBT createInstance(Type type) {
        return new CompoundNBT();
    }
}
