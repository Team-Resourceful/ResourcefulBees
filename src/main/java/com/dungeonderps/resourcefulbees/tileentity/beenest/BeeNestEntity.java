package com.dungeonderps.resourcefulbees.tileentity.beenest;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.beehive.TieredBeehiveTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class BeeNestEntity extends TieredBeehiveTileEntity {
    protected final int TIER = 0;
    protected final float TIER_MODIFIER = 0.5f;

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return RegistryHandler.BEE_NEST_ENTITY.get();
    }

    @Override
    public float getTierModifier() {
        return TIER_MODIFIER;
    }

    @Override
    public int getTier() {
        return TIER;
    }

    @Override
    public boolean isAllowedBee(){
        Block hive = getBlockState().getBlock();
        return hive == RegistryHandler.BEE_NEST.get();
    }
}
