package com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.EliteCentrifugeControllerTileEntity;
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new EliteCentrifugeControllerTileEntity(ModTileEntityTypes.ELITE_CENTRIFUGE_CONTROLLER_ENTITY.get()); }
}



