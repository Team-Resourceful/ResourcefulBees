package com.teamresourceful.resourcefulbees.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxTransfer) {
        super(capacity, maxReceive, maxTransfer);
    }

    protected void onEnergyChanged() {
        //inherit
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onEnergyChanged();
    }

    public boolean canAddEnergy(int amount) {
        return energy + amount <= capacity;
    }

    public void addEnergy(int amount) {
        this.energy += amount;
        if (this.energy > getMaxEnergyStored()) {
            this.energy = getEnergyStored();
        }
        onEnergyChanged();
    }

    public void consumeEnergy(int energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            this.energy = 0;
        }
        onEnergyChanged();
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.energy > getMaxEnergyStored()) {
            this.energy = capacity;
        }
        onEnergyChanged();
    }

    public void setMaxTransfer(int maxTransfer) {
        this.maxExtract = maxTransfer;
    }

    public float getPercentage() {
        return this.getEnergyStored() / (float)this.getMaxEnergyStored();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) setEnergy(tag.getInt("energy"));
    }
}
