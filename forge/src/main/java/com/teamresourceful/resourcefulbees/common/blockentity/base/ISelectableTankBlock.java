package com.teamresourceful.resourcefulbees.common.blockentity.base;

import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import net.minecraftforge.fluids.FluidStack;

public interface ISelectableTankBlock {

    default void setFluid(int tank, FluidStack fluid) {
        getTank(tank).setFluid(fluid);
    }

    SelectableMultiFluidTank getTank(int tank);
}
