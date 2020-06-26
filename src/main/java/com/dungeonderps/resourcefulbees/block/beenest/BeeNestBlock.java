package com.dungeonderps.resourcefulbees.block.beenest;

import com.dungeonderps.resourcefulbees.block.beehive.TieredBeehiveBlock;
import com.dungeonderps.resourcefulbees.tileentity.beenest.BeeNestEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BeeNestBlock extends TieredBeehiveBlock {
    public BeeNestBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BeeNestEntity();
    }
}
