package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.RegistryObject;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeEnergyPortEntity extends AbstractTieredCentrifugeEntity implements IEnergyStorage {

    public CentrifugeEnergyPortEntity(RegistryObject<TileEntityType<CentrifugeEnergyPortEntity>> tileType, CentrifugeTier tier) {
        super(tileType.get(), tier);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(CapabilityEnergy.ENERGY) ? LazyOptional.of(() -> this).cast() : super.getCapability(cap, side);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if(controller == null || controller.assemblyState() != MultiblockController.AssemblyState.ASSEMBLED) return 0;
        int energyReceived = Math.min(tier.getEnergyReceiveRate(), Math.min(controller.getEnergyStorage().getMaxTransfer(), maxReceive));
        if (!simulate) controller.getEnergyStorage().storeEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return controller != null ? controller.getEnergyStorage().getStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return controller != null ? controller.getEnergyStorage().getCapacity() : 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
