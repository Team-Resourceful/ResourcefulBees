package com.teamresourceful.resourcefulbees.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class InternalFluidTank extends FluidTank {

    private final TileEntity tileEntity;

    public InternalFluidTank(int capacity, Predicate<FluidStack> validator, TileEntity tileEntity) {
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
