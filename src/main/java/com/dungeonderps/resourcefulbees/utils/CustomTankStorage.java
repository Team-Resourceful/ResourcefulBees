package com.dungeonderps.resourcefulbees.utils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CustomTankStorage extends FluidTank implements INBTSerializable<CompoundNBT> {

    public CustomTankStorage(int capacity) {
        super(capacity);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        this.fluid.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        fluid = FluidStack.loadFluidStackFromNBT(nbt);
    }
}
