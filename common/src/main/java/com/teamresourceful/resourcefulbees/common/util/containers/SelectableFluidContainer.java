package com.teamresourceful.resourcefulbees.common.util.containers;

import com.teamresourceful.resourcefullib.common.collections.SelectableList;
import earth.terrarium.botarium.Botarium;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidSnapshot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.function.Predicate;

public class SelectableFluidContainer implements FluidContainer {

    public SelectableList<FluidHolder> storedFluid = SelectableList.of(FluidHolder.empty());
    public final long maxAmount;
    public final Predicate<FluidHolder> fluidFilter;

    public SelectableFluidContainer(long maxAmount, Predicate<FluidHolder> fluidFilter) {
        this.maxAmount = maxAmount;
        this.fluidFilter = fluidFilter;
    }

    public void setIndex(int index) {
        this.storedFluid.setSelectedIndex(index);
    }

    public void setFluid(FluidHolder fluid) {
        for (int i = 0; i < this.storedFluid.size(); i++) {
            if (this.storedFluid.get(i).matches(fluid)) {
                setIndex(i);
                return;
            }
        }
    }

    public FluidHolder getFluid() {
        return storedFluid.getSelected();
    }

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        long total = getFluidAmount();
        if (total >= maxAmount) return 0;
        for (int i = 0; i < this.storedFluid.size(); i++) {
            if (fluidFilter.test(fluid)) {
                if (storedFluid.get(i).isEmpty()) {
                    FluidHolder insertedFluid = fluid.copyHolder();
                    insertedFluid.setAmount((long) Mth.clamp(fluid.getFluidAmount(), 0D, maxAmount - total));
                    if (simulate) return insertedFluid.getFluidAmount();
                    this.storedFluid.set(i, insertedFluid);
                    return storedFluid.get(i).getFluidAmount();
                } else {
                    if (storedFluid.get(i).matches(fluid)) {
                        long insertedAmount = (long) Mth.clamp(fluid.getFluidAmount(), 0D, maxAmount - total);
                        if (simulate) return insertedAmount;
                        this.storedFluid.get(i).setAmount(storedFluid.get(i).getFluidAmount() + insertedAmount);
                        return insertedAmount;
                    }
                }
            }
        }
        long amount = (long) Mth.clamp(fluid.getFluidAmount(), 0D, maxAmount - total);
        if (simulate) return amount;
        this.storedFluid.add(fluid.copyWithAmount(amount));
        return amount;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        for (int i = 0; i < this.storedFluid.size(); i++) {
            if (fluidFilter.test(fluid)) {
                FluidHolder toExtract = fluid.copyHolder();
                if (storedFluid.isEmpty()) {
                    return FluidHolder.empty();
                } else if (storedFluid.get(i).matches(fluid)) {
                    long extractedAmount = (long) Mth.clamp(fluid.getFluidAmount(), 0, storedFluid.get(i).getFluidAmount());
                    toExtract.setAmount(extractedAmount);
                    if (simulate) return toExtract;
                    this.storedFluid.get(i).setAmount(storedFluid.get(i).getFluidAmount() - extractedAmount);
                    if (storedFluid.get(i).getFluidAmount() == 0) storedFluid.set(i, FluidHolder.empty());
                    return toExtract;
                }
            }
        }
        return FluidHolder.empty();
    }

    @Override
    public long internalInsert(FluidHolder fluid, boolean simulate) {
        return insertFluid(fluid, simulate);
    }

    @Override
    public FluidHolder internalExtract(FluidHolder fluid, boolean simulate) {
        return extractFluid(fluid, simulate);
    }

    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toInsert, Runnable snapshot) {
        if (Objects.equals(fluidHolder.getCompound(), toInsert.getCompound()) && fluidHolder.getFluid().isSame(toInsert.getFluid())) {
            long extracted = (long) Mth.clamp(toInsert.getFluidAmount(), 0, fluidHolder.getFluidAmount());
            snapshot.run();
            fluidHolder.setAmount(fluidHolder.getFluidAmount() - extracted);
            if (fluidHolder.getFluidAmount() == 0) fluidHolder.setFluid(Fluids.EMPTY);
            return extracted;
        }
        return 0;
    }

    @Override
    public boolean allowsInsertion() {
        return true;
    }

    @Override
    public boolean allowsExtraction() {
        return true;
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return new SimpleFluidSnapshot(this);
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        this.storedFluid.set(slot, fluid);
    }

    @Override
    public SelectableList<FluidHolder> getFluids() {
        return storedFluid;
    }

    @Override
    public int getSize() {
        return getFluids().size();
    }

    @Override
    public boolean isEmpty() {
        if (getFluids().isEmpty()) return true;
        for (FluidHolder fluidHolder : getFluids()) {
            if (!fluidHolder.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public FluidContainer copy() {
        return new SelectableFluidContainer(maxAmount, fluidFilter);
    }

    @Override
    public long getTankCapacity(int tankSlot) {
        return maxAmount;
    }

    @Override
    public void fromContainer(FluidContainer container) {
        this.storedFluid = SelectableList.of(FluidHolder.empty());
        for (FluidHolder fluidHolder : container.getFluids()) {
            this.storedFluid.add(fluidHolder.copyHolder());
        }
    }


    @Override
    public void deserialize(CompoundTag nbt) {
        CompoundTag tag = nbt.getCompound(Botarium.BOTARIUM_DATA);
        CompoundTag fluidTag = tag.getCompound("FluidData");
        ListTag fluids = fluidTag.getList(SimpleFluidContainer.FLUID_KEY, Tag.TAG_COMPOUND);
        this.storedFluid = SelectableList.of(FluidHolder.empty());
        for (int i = 0; i < fluids.size(); i++) {
            CompoundTag fluid = fluids.getCompound(i);
            this.storedFluid.add(FluidHolder.fromCompound(fluid));
        }
        this.storedFluid.setSelectedIndex(fluidTag.getInt("Index"));
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        CompoundTag tag = nbt.getCompound(Botarium.BOTARIUM_DATA);
        CompoundTag fluidTag = new CompoundTag();
        if (!this.storedFluid.isEmpty()) {
            ListTag tags = new ListTag();
            for (FluidHolder fluidHolder : this.storedFluid) {
                tags.add(fluidHolder.serialize());
            }
            fluidTag.put(SimpleFluidContainer.FLUID_KEY, tags);
        }
        fluidTag.putInt("Index", this.storedFluid.getSelectedIndex());
        tag.put("FluidData", fluidTag);
        nbt.put(Botarium.BOTARIUM_DATA, tag);
        return nbt;
    }

    @Override
    public void clearContent() {
        this.storedFluid = SelectableList.of(FluidHolder.empty());
    }

    public long getFluidAmount() {
        long amount = 0;
        for (FluidHolder holder : this.storedFluid) {
            amount += holder.getFluidAmount();
        }
        return amount;
    }

    public boolean isFull() {
        return getFluidAmount() >= maxAmount;
    }
}
