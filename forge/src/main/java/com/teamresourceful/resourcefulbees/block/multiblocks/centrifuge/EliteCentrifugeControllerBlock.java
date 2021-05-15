package com.teamresourceful.resourcefulbees.block.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeControllerTileEntity;

import javax.annotation.Nullable;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EliteCentrifugeControllerBlock extends CentrifugeControllerBlock {
    public EliteCentrifugeControllerBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) { return new EliteCentrifugeControllerTileEntity(ModBlockEntityTypes.ELITE_CENTRIFUGE_CONTROLLER_ENTITY.get()); }
}



