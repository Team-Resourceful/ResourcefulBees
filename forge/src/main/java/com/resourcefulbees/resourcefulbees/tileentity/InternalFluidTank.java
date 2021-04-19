package com.resourcefulbees.resourcefulbees.tileentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class InternalFluidTank extends FluidTank {

    private final BlockEntity tileEntity;

    public InternalFluidTank(int capacity, Predicate<FluidStack> validator, BlockEntity tileEntity) {
        super(capacity, validator);
        this.tileEntity = tileEntity;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        if (tileEntity.getLevel() != null) {
            BlockState state = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos());
            tileEntity.getLevel().sendBlockUpdated(tileEntity.getBlockPos(), state, state, 2);
        }
    }
}
