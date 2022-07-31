package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.RegistryObject;
import net.roguelogix.phosphophyllite.multiblock2.MultiblockController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeEnergyPortEntity extends AbstractTieredCentrifugeEntity implements IEnergyStorage {

    public CentrifugeEnergyPortEntity(RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(CapabilityEnergy.ENERGY) ? LazyOptional.of(() -> this).cast() : super.capability(cap, side);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        controller(); //why is this method here?...
        if(controller().assemblyState() != MultiblockController.AssemblyState.ASSEMBLED) return 0;
        int energyReceived = Math.min(tier.getEnergyReceiveRate(), Math.min(controller().getEnergyStorage().getMaxTransfer(), maxReceive));
        if (!simulate) controller().getEnergyStorage().storeEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return controller().getEnergyStorage().getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return controller().getEnergyStorage().getCapacity();
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
