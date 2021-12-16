package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class WaxedSignTileEntity extends SignTileEntity {
    @Override
    public TileEntityType<?> getType() {
        return ModTileEntityTypes.WAXED_SIGN_TILE_ENTITY.get();
    }
}
