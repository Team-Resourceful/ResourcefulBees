package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefullib.common.collections.SelectableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectableMultiFluidTank implements IFluidTank, IFluidHandler, INBTSerializable<CompoundTag> {

    protected final int capacity;
    protected SelectableList<FluidStack> fluids = SelectableList.of(FluidStack.EMPTY);
    protected final Predicate<FluidStack> validator;

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

    public void setFluid(FluidStack fluid) {
        for (int i = 0; i < this.fluids.size(); i++) {
            if (this.fluids.get(i).isFluidEqual(fluid)) {
                setIndex(i);
                return;
            }
        }
    }

    private int getTotalAmount() {
        int amount = 0;
        for (FluidStack fluid : fluids) amount += fluid.getAmount();
        return amount;
    }

    public boolean isFull() {
        return getTotalAmount() >= capacity;
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

    public SelectableList<FluidStack> getFluids() {
        return fluids;
    }

    //region Fill
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
        return forceFill(resource, action, false);
    }

    public int forceFill(FluidStack resource, FluidAction action, boolean forced) {
        if (resource.isEmpty() || (!isFluidValid(resource) && !forced)) return 0;
        if (getTotalAmount() == capacity) return 0;
        if (action.simulate()) {
            if (fluids.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            return Math.min(capacity - getTotalAmount(), resource.getAmount());
        }
        return addOrUpdateStack(resource);
    }
    //endregion

    //region Drain
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
        Iterator<FluidStack> iterator = fluids.iterator();
        while (iterator.hasNext()) {
            FluidStack fluid = iterator.next();
            if (stack.isFluidEqual(fluid)) {
                int drained = Math.min(stack.getAmount(), drain);
                FluidStack newStack = new FluidStack(fluid, drained);
                if (action.execute() && drained > 0) {
                    fluid.shrink(drained);
                }
                if (fluid.isEmpty()) {
                    iterator.remove();
                }
                return newStack;
            }
        }

        return FluidStack.EMPTY;
    }
    //endregion

    //region NBT
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Index", this.fluids.getSelectedIndex());
        ListTag listTag = new ListTag();
        for (FluidStack fluid : this.fluids) {
            listTag.add(fluid.writeToNBT(new CompoundTag()));
        }
        tag.put("Fluids", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        List<FluidStack> nbtFluids = new ArrayList<>();
        for (Tag tag : nbt.getList("Fluids", Tag.TAG_COMPOUND)) {
            nbtFluids.add(FluidStack.loadFluidStackFromNBT((CompoundTag)tag));
        }
        if (!nbtFluids.isEmpty()) {
            List<FluidStack> fluids = nbtFluids.stream().filter(Predicate.not(FluidStack::isEmpty)).collect(Collectors.toList());
            FluidStack selectedFluid = nbtFluids.get(nbt.getInt("Index"));
            this.fluids = new SelectableList<>(FluidStack.EMPTY, fluids);
            this.fluids.setSelectedIndex(Math.max(fluids.indexOf(selectedFluid), 0));
        }
    }
    //endregion
}
