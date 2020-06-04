
package com.dungeonderps.resourcefulbees.tileentity.beehive;


import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class Tier3BeehiveBlockEntity extends Tier1BeehiveBlockEntity {
  protected final int TIER = 3;
  protected final float TIER_MODIFIER = 2f;

  @Nonnull
  @Override
  public TileEntityType<?> getType() {
    return RegistryHandler.T3_BEEHIVE_ENTITY.get();
  }

  @Override
  public int getTier() {
    return TIER;
  }

  @Override
  public float getTierModifier() {
    return TIER_MODIFIER;
  }

  @Override
  public boolean isAllowedBee(){
    Block hive = getBlockState().getBlock();
    return hive == RegistryHandler.T3_BEEHIVE.get();
  }
}
