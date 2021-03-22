package com.resourcefulbees.resourcefulbees.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

//TODO 1.17 Do we need a Custom class for this and if not lets remove the custom class

public class CustomEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

    public CustomEnergyStorage(int capacity, int maxRecieve, int maxTransfer) {
        super(capacity, maxRecieve, maxTransfer);
    }

    protected void onEnergyChanged() {
        //inherit
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onEnergyChanged();
    }

    public void addEnergy(int energy) {
        this.energy += energy;
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

    @Override
    public Tag serializeNBT() {
        Tag tag = new CompoundNBT();
        tag.putInt("energy", getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        setEnergy(nbt.getInt("energy"));
    }
}
