package com.resourcefulbees.resourcefulbees.fluids;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;

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
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(@NotNull StateContainer.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends HoneyFlowingFluid {

        public Source(Properties properties, HoneyBottleData honeyData) {
            super(properties, honeyData);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}
