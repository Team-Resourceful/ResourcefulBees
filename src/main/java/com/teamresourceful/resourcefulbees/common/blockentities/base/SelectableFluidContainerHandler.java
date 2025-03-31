package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.common.util.containers.SelectableFluidContainer;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;

public interface SelectableFluidContainerHandler extends BotariumFluidBlock<WrappedBlockFluidContainer> {

    default void setFluid(FluidHolder fluid) {
        getSelectableFluidContainer().setFluid(fluid);
    }

    SelectableFluidContainer getSelectableFluidContainer();
}
