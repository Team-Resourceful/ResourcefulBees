package com.resourcefulbees.resourcefulbees.container.sync;

import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraftforge.energy.CapabilityEnergy;

public class SyncPower {

    public static IIntArray getSyncableArray(TileEntity tileEntity, int energy) {

        return new IIntArray() {
            @Override
            public int get(int index) {
                return index == 0 ? energy & 0xffff : (energy >> 16) & 0xffff;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                        int energyStored = h.getEnergyStored() & 0xffff0000;
                        ((CustomEnergyStorage) h).setEnergy(energyStored + (value & 0xffff));
                    });
                } else {
                    tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                        int energyStored = h.getEnergyStored() & 0x0000ffff;
                        ((CustomEnergyStorage)h).setEnergy(energyStored | (value << 16));
                    });
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }
}
