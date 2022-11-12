package com.teamresourceful.resourcefulbees.common.blockentity.base;

import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import net.minecraftforge.fluids.FluidStack;

public interface SelectableFluidContainerHandler {

    default void setFluid(FluidStack fluid) {
        getContainer().setFluid(fluid);
    }
    default void setFluid(int tank, FluidStack fluid) {
        getContainer(tank).setFluid(fluid);
    }

    default SelectableMultiFluidTank getContainer() {
        return getContainer(0);
    }

    SelectableMultiFluidTank getContainer(int tank);
}
