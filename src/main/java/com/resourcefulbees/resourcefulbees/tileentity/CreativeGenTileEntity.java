package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class CreativeGenTileEntity extends TileEntity implements ITickableTileEntity {

    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public CreativeGenTileEntity() { super(ModTileEntityTypes.CREATIVE_GEN_ENTITY.get()); }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.energyOptional.invalidate();
    }

    private void sendOutPower() {
        if (world != null && !world.isRemote) {
            Arrays.stream(Direction.values())
                    .map(direction -> world.getTileEntity(pos.offset(direction)))
                    .filter(Objects::nonNull)
                    .forEach(tileEntity -> tileEntity.getCapability(CapabilityEnergy.ENERGY)
                            .map(iEnergyStorage -> {
                                if (iEnergyStorage.canReceive()) {
                                    return iEnergyStorage.receiveEnergy(Integer.MAX_VALUE, false) != 0;
                                }
                                return true;
                            }));
        }
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Integer.MAX_VALUE, 0, Integer.MAX_VALUE) {
            @Override
            protected void onEnergyChanged() { markDirty(); }
        };
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            energyStorage.setEnergy(Integer.MAX_VALUE);
            sendOutPower();
        }
    }
}
