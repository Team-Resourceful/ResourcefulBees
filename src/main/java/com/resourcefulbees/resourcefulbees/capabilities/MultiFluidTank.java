package com.resourcefulbees.resourcefulbees.capabilities;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class MultiFluidTank implements IFluidHandler {

    protected FluidTank[] fluidTanks;

    public MultiFluidTank(int capacity, int numberOfTanks) { this(capacity, numberOfTanks, fluidStack -> true); }

    public MultiFluidTank(int capacity, int numberOfTanks, Predicate<FluidStack> validator) {
         fluidTanks = new FluidTank[numberOfTanks];
        for (int i = 0; i < numberOfTanks; i++) {
            fluidTanks[i] = new FluidTank(capacity, validator);
        }
    }

    @Override
    public int getTanks() { return fluidTanks.length; }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) { return fluidTanks[tank].getFluid(); }

    @Override
    public int getTankCapacity(int tank) { return fluidTanks[tank].getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return fluidTanks[tank].isFluidValid(stack); }

    public int getFluidAmountInTank(int tank) {
        return fluidTanks[tank].getFluidAmount();
    }

    public void setFluidInTank(int tank, FluidStack fluidStack) {
        fluidTanks[tank].setFluid(fluidStack);
    }

    /**
     *  DO NOT USE!!
     */
    @Deprecated
    @Override
    public int fill(FluidStack resource, FluidAction action) { return 0; }

    public int fill(int tank, FluidStack resource, FluidAction action) {
        return fluidTanks[tank].fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        for (FluidTank fluidTank : fluidTanks) {
            if (resource.isFluidEqual(fluidTank.getFluid())) {
                return fluidTank.drain(resource, action);
            }
        }

        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        Optional<FluidTank> largestTank = Arrays.stream(fluidTanks).max(Comparator.comparingInt(FluidTank::getFluidAmount));
        return largestTank.map(fluidTank -> fluidTank.drain(maxDrain, action)).orElse(FluidStack.EMPTY);
    }

    /**
     * Drains fluid from specific internal tank. Convenience method added for mod integrations.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    public FluidStack drain(FluidStack resource, FluidAction action, int tank) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        return fluidTanks[tank].drain(resource, action);
    }

    /**
     * Drains fluid from specific internal tank. Convenience method added for mod integrations.
     *
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    public FluidStack drain(int maxDrain, FluidAction action, int tank) {
        if (fluidTanks[tank].getFluidAmount() > 0) {
            return fluidTanks[tank].drain(maxDrain, action);
        }

        return FluidStack.EMPTY;
    }

    public void readFromNBT(CompoundNBT nbt) {
        ListNBT listNBT = nbt.getList(NBTConstants.NBT_TANKS, 10);

        if (!listNBT.isEmpty()) {
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT tank = listNBT.getCompound(i);
                fluidTanks[i].readFromNBT(tank);
            }
        }
    }

    public ListNBT writeToNBT() {
        ListNBT listNBT = new ListNBT();
        int i = 0;
        for (FluidTank fluidTank : fluidTanks) {
            listNBT.add(i++, fluidTank.writeToNBT(new CompoundNBT()));
        }
        return listNBT;
    }
}
