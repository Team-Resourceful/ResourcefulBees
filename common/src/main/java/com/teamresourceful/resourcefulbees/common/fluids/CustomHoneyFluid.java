package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import earth.terrarium.botarium.common.registry.fluid.BotariumFlowingFluid;
import earth.terrarium.botarium.common.registry.fluid.BotariumSourceFluid;
import earth.terrarium.botarium.common.registry.fluid.FluidData;

public interface CustomHoneyFluid {

    HoneyFluidData getHoneyData();

    class Flowing extends BotariumFlowingFluid implements CustomHoneyFluid {

        private final HoneyFluidData data;

        public Flowing(HoneyFluidData honeyData, FluidData data) {
            super(data);
            this.data = honeyData;
        }

        @Override
        public HoneyFluidData getHoneyData() {
            return data;
        }
    }

    class Source extends BotariumSourceFluid implements CustomHoneyFluid {

        private final HoneyFluidData data;

        public Source(HoneyFluidData honeyData, FluidData data) {
            super(data);
            this.data = honeyData;
        }

        @Override
        public HoneyFluidData getHoneyData() {
            return data;
        }
    }
}
