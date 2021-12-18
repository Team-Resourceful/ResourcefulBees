package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class CreativeGenBlockEntity extends BlockEntity {

    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public CreativeGenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY.get(), pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        this.energyOptional.invalidate();
    }

    private static void sendOutPower(Level level, BlockPos pos) {
        if (level != null && !level.isClientSide) {
            Arrays.stream(Direction.values())
                    .map(direction -> level.getBlockEntity(pos.relative(direction)))
                    .filter(Objects::nonNull)
                    .forEach(tileEntity -> tileEntity.getCapability(CapabilityEnergy.ENERGY)
                    .map(iEnergyStorage -> !iEnergyStorage.canReceive() || iEnergyStorage.receiveEnergy(Integer.MAX_VALUE, false) != 0));
        }
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Integer.MAX_VALUE, 0, Integer.MAX_VALUE) {
            @Override
            protected void onEnergyChanged() { setChanged(); }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CreativeGenBlockEntity entity) {
        if (level != null) {
            entity.energyStorage.setEnergy(Integer.MAX_VALUE);
            sendOutPower(level, pos);
        }
    }
}
