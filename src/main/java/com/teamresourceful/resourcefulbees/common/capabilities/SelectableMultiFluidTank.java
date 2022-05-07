package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.common.utils.SelectableList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SelectableMultiFluidTank implements IFluidTank, IFluidHandler {

    protected int capacity;
    protected SelectableList<FluidStack> fluids = SelectableList.of(FluidStack.EMPTY);
    protected Predicate<FluidStack> validator;

    public SelectableMultiFluidTank(int capacity) {
        this(capacity, f -> true);
    }

    public SelectableMultiFluidTank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public void setIndex(int index) {
        this.fluids.setSelectedIndex(index);
    }

    private int getTotalAmount() {
        int amount = 0;
        for (FluidStack fluid : fluids) amount += fluid.getAmount();
        return amount;
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return fluids.getSelected();
    }

    @Override
    public int getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack);
    }

    public int addOrUpdateStack(FluidStack stack) {
        if (fluids.isEmpty()) {
            int amount = Math.min(capacity, stack.getAmount());
            fluids.add(new FluidStack(stack, amount));
            return amount;
        }
        for (FluidStack fluid : fluids) {
            if (fluid.isFluidEqual(stack)) {
                int amount = Math.min(capacity - getTotalAmount(), stack.getAmount());
                fluid.grow(amount);
                return amount;
            }
        }
        int amount = Math.min(capacity - getTotalAmount(), stack.getAmount());
        fluids.add(new FluidStack(stack, amount));
        return amount;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) return 0;
        if (getTotalAmount() == capacity) return 0;
        if (action.simulate()) {
            if (fluids.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            return Math.min(capacity - getTotalAmount(), resource.getAmount());
        }
        return addOrUpdateStack(resource);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !fluids.contains(resource)) return FluidStack.EMPTY;
        return drain(resource, resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return drain(getFluid(), maxDrain, action);
    }

    public FluidStack drain(FluidStack stack, int drain, FluidAction action) {
        for (FluidStack fluid : fluids) {
            if (stack.isFluidEqual(fluid)) {
                int drained = Math.min(stack.getAmount(), drain);
                FluidStack newStack = new FluidStack(fluid, drained);
                if (action.execute() && drained > 0) {
                    fluid.shrink(drained);
                }
                return newStack;
            }
        }
        return FluidStack.EMPTY;
    }
}
