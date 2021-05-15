package com.teamresourceful.resourcefulbees.block;

import com.teamresourceful.resourcefulbees.tileentity.AcceleratorTileEntity;

import javax.annotation.Nullable;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new AcceleratorTileEntity();
    }
}
