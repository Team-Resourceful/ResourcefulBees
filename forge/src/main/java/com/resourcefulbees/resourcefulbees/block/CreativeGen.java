package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.tileentity.CreativeGenTileEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CreativeGen extends Block {
    public CreativeGen(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CreativeGenTileEntity();
    }
}
