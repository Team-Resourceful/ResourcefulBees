package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.teamresourceful.resourcefullib.common.inventory.IntContainerData;
import net.minecraft.nbt.CompoundTag;

public class CentrifugeEnergyStorage extends IntContainerData {

    private static final int CAPACITY = 0;
    private static final int STORED = 1;

    //private final int[] energy = new int[2];

    public CentrifugeEnergyStorage() {
        super(2);
    }

    public void reset() {
        setInt(CAPACITY, 0);
        setInt(STORED, 0);
    }

    public int getCapacity() {
        return getInt(CAPACITY);
    }

    public int getStored() {
        return getInt(STORED);
    }

    public void increaseCapacity(int amount) {
        setInt(CAPACITY, getCapacity() + amount);
    }

    public void decreaseCapacity(int amount) {
        int capacity = getCapacity();
        capacity -= amount;
        setInt(CAPACITY, capacity);
        int stored = getStored();
        if (stored > capacity) {
            stored = capacity;
            setInt(STORED, stored);
        }
    }

    public int getMaxTransfer() {
        return getCapacity() - getStored();
    }

    public boolean consumeEnergy(int amount, boolean simulate) {
        int stored = getStored();
        boolean canConsume = amount > 0 && stored - amount > -1;
        if (!simulate && canConsume) {
            stored -= amount;
            setInt(STORED, stored);
        }
        return canConsume;
    }

    public void storeEnergy(int amount) {
        setInt(STORED, getStored() + amount);
    }

    public boolean isEmpty() {
        return getStored() == 0;
    }

    public void serializeNBT(CompoundTag tag) {
        tag.putInt("storedEnergy", getStored());
    }

    public void deserializeNBT(CompoundTag tag) {
        setInt(STORED, tag.getInt("storedEnergy"));
    }

}
