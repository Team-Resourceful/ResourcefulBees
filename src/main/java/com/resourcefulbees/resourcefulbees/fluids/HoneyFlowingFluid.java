package com.resourcefulbees.resourcefulbees.fluids;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class HoneyFlowingFluid extends ForgeFlowingFluid {

    private final HoneyBottleData honeyData;

    protected HoneyFlowingFluid(Properties properties, HoneyBottleData honeyData) {
        super(properties);
        this.honeyData = honeyData;
    }

    public HoneyBottleData getHoneyData() {
        return honeyData;
    }

    public static class Flowing extends HoneyFlowingFluid {

        public Flowing(Properties properties, HoneyBottleData honeyData) {
            super(properties, honeyData);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        @Override
        protected void fillStateContainer(@NotNull StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends HoneyFlowingFluid {

        public Source(Properties properties, HoneyBottleData honeyData) {
            super(properties, honeyData);
        }

        public int getLevel(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}
