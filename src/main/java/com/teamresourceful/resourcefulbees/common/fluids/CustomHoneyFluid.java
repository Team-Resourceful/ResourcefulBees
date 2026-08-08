package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;

public class CustomHoneyFluid {
    private CustomHoneyFluid() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static class Flowing extends ResourcefulFlowingFluid.Flowing {

        private final HoneyFluidData honeyFluidData;

        public Flowing(HoneyFluidData honeyFluidData, FluidData data) {
            super(data);
            this.honeyFluidData = honeyFluidData;
        }

        public HoneyFluidData getHoneyFluidData() {
            return honeyFluidData;
        }
    }

    public static class Still extends ResourcefulFlowingFluid.Still {

        private final HoneyFluidData honeyFluidData;

        public Still(HoneyFluidData honeyFluidData, FluidData data) {
            super(data);
            this.honeyFluidData = honeyFluidData;
        }

        public HoneyFluidData getHoneyFluidData() {
            return honeyFluidData;
        }
    }


}
