package com.dungeonderps.resourcefulbees.block.beenest;

import com.dungeonderps.resourcefulbees.tileentity.beenest.JungleBeeNest;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class JungleBeeNestBlock extends BeeNestBlock {
    public JungleBeeNestBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new JungleBeeNest();
    }

}
