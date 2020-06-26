package com.dungeonderps.resourcefulbees.block.beehive;

import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier4BeehiveTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class Tier4BeehiveBlock extends TieredBeehiveBlock {

  public Tier4BeehiveBlock(Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new Tier4BeehiveTileEntity();
  }
}
