package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.RegistryObject;
import net.roguelogix.phosphophyllite.multiblock2.validated.IValidatedMultiblock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeEnergyPortEntity extends AbstractTieredCentrifugeEntity implements IEnergyStorage {

    public CentrifugeEnergyPortEntity(RegistryObject<BlockEntityType<CentrifugeEnergyPortEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.ENERGY) ? LazyOptional.of(() -> this).cast() : super.capability(cap, side);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        var controller = nullableController();
        if (controller == null) return 0;
        if(controller.assemblyState() != IValidatedMultiblock.AssemblyState.ASSEMBLED) return 0;
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
        var controller = nullableController();
        return controller == null ? 0 : controller.getEnergyStorage().getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        var controller = nullableController();
        return controller == null ? 0 : controller.getEnergyStorage().getCapacity();
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
