package com.resourcefulbees.resourcefulbees.block.centrifuge;

import com.resourcefulbees.resourcefulbees.tileentity.centrifuge.CentrifugeCasingTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class CentrifugeCasingBlock extends Block {
    public CentrifugeCasingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CentrifugeCasingTileEntity();
    }
}
