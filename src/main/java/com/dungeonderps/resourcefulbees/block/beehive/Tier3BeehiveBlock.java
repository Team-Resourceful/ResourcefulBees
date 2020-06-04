package com.dungeonderps.resourcefulbees.block.beehive;

import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier3BeehiveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class Tier3BeehiveBlock extends Tier1BeehiveBlock {

  public Tier3BeehiveBlock(Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new Tier3BeehiveBlockEntity();
  }
}
