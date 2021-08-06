package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.tileentity.AcceleratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;


public class AcceleratorBlock extends Block {
    public AcceleratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AcceleratorTileEntity();
    }
}
