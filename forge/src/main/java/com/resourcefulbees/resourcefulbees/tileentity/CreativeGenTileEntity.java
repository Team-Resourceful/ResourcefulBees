package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class CreativeGenTileEntity extends BlockEntity implements TickableBlockEntity {

    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public CreativeGenTileEntity() { super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY.get()); }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.energyOptional.invalidate();
    }

    private void sendOutPower() {
        if (level != null && !level.isClientSide) {
            Arrays.stream(Direction.values())
                    .map(direction -> level.getBlockEntity(worldPosition.relative(direction)))
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
            protected void onEnergyChanged() { setChanged(); }
        };
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            energyStorage.setEnergy(Integer.MAX_VALUE);
            sendOutPower();
        }
    }
}
