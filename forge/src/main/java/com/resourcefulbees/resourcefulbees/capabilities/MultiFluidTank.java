package com.resourcefulbees.resourcefulbees.capabilities;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) { return fluidTanks[tank].getFluid(); }

    @Override
    public int getTankCapacity(int tank) { return fluidTanks[tank].getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return fluidTanks[tank].isFluidValid(stack); }

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

    @NotNull
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

    @NotNull
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
    @NotNull
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
    @NotNull
    public FluidStack drain(int maxDrain, FluidAction action, int tank) {
        if (fluidTanks[tank].getFluidAmount() > 0) {
            return fluidTanks[tank].drain(maxDrain, action);
        }

        return FluidStack.EMPTY;
    }

    public void readFromNBT(CompoundTag nbt) {
        ListTag listNBT = nbt.getList(NBTConstants.NBT_TANKS, 10);

        if (!listNBT.isEmpty()) {
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundTag tank = listNBT.getCompound(i);
                fluidTanks[i].readFromNBT(tank);
            }
        }
    }

    public Tag writeToNBT() {
        ListTag listNBT = new ListTag();
        int i = 0;
        for (FluidTank fluidTank : fluidTanks) {
            listNBT.add(i++, fluidTank.writeToNBT(new CompoundTag()));
        }
        return listNBT;
    }
}
