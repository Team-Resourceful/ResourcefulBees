package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyBlock;
import earth.terrarium.botarium.common.energy.impl.ExtractOnlyEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
import earth.terrarium.botarium.common.energy.util.EnergyHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeGenBlockEntity extends BlockEntity implements InstanceBlockEntityTicker, BotariumEnergyBlock<WrappedBlockEnergyContainer> {

    private WrappedBlockEnergyContainer energy;

    public CreativeGenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CREATIVE_GEN_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level != null) {
            this.energy.setEnergy(Integer.MAX_VALUE);
            EnergyHooks.distributeEnergyNearby(this);
        }
    }

    @Override
    public WrappedBlockEnergyContainer getEnergyStorage() {
        if (energy == null) {
            energy = new WrappedBlockEnergyContainer(this, new ExtractOnlyEnergyContainer(Integer.MAX_VALUE));
        }
        return null;
    }
}
