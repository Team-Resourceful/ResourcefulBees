package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;

public class CentrifugeEnergyStorage implements ContainerData {

    private static final int CAPACITY = 0;
    private static final int STORED = 1;

    private final int[] energy = new int[2];

    public void reset() {
        energy[CAPACITY] = 0;
        energy[STORED] = 0;
    }

    public int getCapacity() {
        return energy[CAPACITY];
    }

    public int getStored() {
        return energy[STORED];
    }

    public void increaseCapacity(int amount) {
        energy[CAPACITY] += amount;
    }

    public void decreaseCapacity(int amount) {
        energy[CAPACITY] -= amount;
        if (energy[STORED] > energy[CAPACITY]) energy[STORED] = energy[CAPACITY];
    }

    public int getMaxTransfer() {
        return energy[CAPACITY] - energy[STORED];
    }

    public boolean consumeEnergy(int amount, boolean simulate) {
        boolean canConsume = amount > 0 && energy[STORED] - amount > -1;
        if (!simulate && canConsume) energy[STORED] -= amount;
        return canConsume;
    }

    public void storeEnergy(int amount) {
        energy[STORED] += amount;
    }

    public boolean isEmpty() {
        return energy[STORED] == 0;
    }

    public void serializeNBT(CompoundTag tag) {
        tag.putInt("storedEnergy", energy[STORED]);
    }

    public void deserializeNBT(CompoundTag tag) {
        energy[STORED] = tag.getInt("storedEnergy");
    }

    @Override
    public int get(int index) {
        return inBounds(index) ? energy[index] : -1;
    }

    @Override
    public void set(int index, int value) {
        if (!inBounds(index)) return;
        energy[index] = value;
    }

    private boolean inBounds(int index) {
        return MathUtils.inRangeInclusive(index, 0, getCount()-1);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
