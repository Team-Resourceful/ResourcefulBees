package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import net.minecraft.nbt.CompoundTag;

public class CentrifugeEnergyStorage {

    private int capacity = 0;
    private int stored = 0;

    public void reset() {
        capacity = 0;
        stored = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStored() {
        return stored;
    }

    public void increaseCapacity(int amount) {
        capacity += amount;
    }

    public void decreaseCapacity(int amount) {
        capacity -= amount;
        if (stored > capacity) stored = capacity;
    }

    public int getMaxTransfer() {
        return capacity - stored;
    }

    public boolean consumeEnergy(int amount, boolean simulate) {
        boolean canConsume = amount > 0 && stored - amount > -1;
        if (!simulate && canConsume) stored -= amount;
        return canConsume;
    }

    public void storeEnergy(int amount) {
        stored += amount;
    }


    public void serializeNBT(CompoundTag tag) {
        tag.putInt("storedEnergy", stored);
    }

    public void deserializeNBT(CompoundTag tag) {
        stored = tag.getInt("storedEnergy");
    }
}
