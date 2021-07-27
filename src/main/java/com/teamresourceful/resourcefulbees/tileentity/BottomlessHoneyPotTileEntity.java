package com.teamresourceful.resourcefulbees.tileentity;

import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BottomlessHoneyPotTileEntity extends AbstractHoneyTank implements ITickableTileEntity {

    private boolean doEmpty = false;
    private static final int CAPACITY = 64000;

    public BottomlessHoneyPotTileEntity() {
        super(ModBlockEntityTypes.BOTTOMLESS_HONEY_POT_TILE_ENTITY.get());
        setFluidTank(new VoidHoneyTank(CAPACITY, honeyFluidPredicate(), this));
        setFluidOptional(LazyOptional.of(this::getFluidTank));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (side != Direction.UP && side != Direction.DOWN) return LazyOptional.empty();
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if (level == null) return;
        if (this.level.getGameTime() % 80L == 0 && doEmpty) {
            getFluidTank().drain(CAPACITY, IFluidHandler.FluidAction.EXECUTE);
            doEmpty = false;
        }
    }

    private static class VoidHoneyTank extends FluidTank {

        private final BottomlessHoneyPotTileEntity tileEntity;

        public VoidHoneyTank(int capacity, Predicate<FluidStack> validator, BottomlessHoneyPotTileEntity tileEntity) {
            super(capacity, validator);
            this.tileEntity = tileEntity;
        }

        @Override
        protected void onContentsChanged() {
            if (!fluid.isEmpty()) tileEntity.doEmpty = true;
        }
    }
}
