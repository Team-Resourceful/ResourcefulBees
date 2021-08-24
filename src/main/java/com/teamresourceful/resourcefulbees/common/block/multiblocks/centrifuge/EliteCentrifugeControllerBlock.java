package com.teamresourceful.resourcefulbees.common.block.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.centrifuge.EliteCentrifugeControllerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class EliteCentrifugeControllerBlock extends CentrifugeControllerBlock {
    public EliteCentrifugeControllerBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EliteCentrifugeControllerTileEntity(ModBlockEntityTypes.ELITE_CENTRIFUGE_CONTROLLER_ENTITY.get());
    }
}



