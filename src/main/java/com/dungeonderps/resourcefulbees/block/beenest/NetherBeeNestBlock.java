package com.dungeonderps.resourcefulbees.block.beenest;

import com.dungeonderps.resourcefulbees.tileentity.beenest.NetherBeeNest;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class NetherBeeNestBlock extends BeeNestBlock {
    public NetherBeeNestBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NetherBeeNest();
    }

}
