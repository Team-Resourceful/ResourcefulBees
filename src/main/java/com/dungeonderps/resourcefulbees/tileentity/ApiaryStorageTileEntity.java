package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class ApiaryStorageTileEntity extends TileEntity {
    public ApiaryStorageTileEntity() {
        super(RegistryHandler.APIARY_STORAGE_TILE_ENTITY.get());
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return RegistryHandler.APIARY_STORAGE_TILE_ENTITY.get();
    }
}
