package com.dungeonderps.resourcefulbees.block.beenest;

import com.dungeonderps.resourcefulbees.tileentity.beenest.AcaciaBeeNest;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class AcaciaBeeNestBlock extends BeeNestBlock {
    public AcaciaBeeNestBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AcaciaBeeNest();
    }
}
