package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.common.util.containers.SelectableFluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;

public interface SelectableFluidContainerHandler {

    default void setFluid(FluidHolder fluid) {
        getSelectableFluidContainer().setFluid(fluid);
    }

    SelectableFluidContainer getSelectableFluidContainer();
}
