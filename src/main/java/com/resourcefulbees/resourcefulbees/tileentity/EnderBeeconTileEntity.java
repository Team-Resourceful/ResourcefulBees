package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraftforge.common.ToolType;

public class EnderBeeconTileEntity extends TileEntity implements ITickableTileEntity {

    public static final int RANGE = 10;

    public EnderBeeconTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public static MutableBoundingBox getDistruptorBox(BlockPos pos){
        return MutableBoundingBox.createProper(pos.getX() + RANGE, pos.getY() + RANGE, pos.getZ() + RANGE, pos.getX() - RANGE, pos.getY() - RANGE, pos.getZ() - RANGE);
    }

    @Override
    public void tick() {

    }
}
