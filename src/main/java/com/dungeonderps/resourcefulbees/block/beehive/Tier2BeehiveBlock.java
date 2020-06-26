package com.dungeonderps.resourcefulbees.block.beehive;

import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier2BeehiveTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class Tier2BeehiveBlock extends TieredBeehiveBlock {

  public Tier2BeehiveBlock(Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new Tier2BeehiveTileEntity();
  }
}
