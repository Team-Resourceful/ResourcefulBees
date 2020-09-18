package com.resourcefulbees.resourcefulbees.lib;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CustomStorageContainers {

    public static class CustomTankStorage extends FluidTank implements INBTSerializable<CompoundNBT> {

        public CustomTankStorage(int capacity) {
            super(capacity);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            this.fluid.writeToNBT(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            fluid = FluidStack.loadFluidStackFromNBT(nbt);
        }
    }

    public static class CustomEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

        public CustomEnergyStorage(int capacity, int maxRecieve, int maxTransfer) {
            super(capacity, maxRecieve, maxTransfer);
        }

        protected void onEnergyChanged() {

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
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("energy", getEnergyStored());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            setEnergy(nbt.getInt("energy"));
        }
    }
}
