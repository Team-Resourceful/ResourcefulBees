package com.dungeonderps.resourcefulbees.container;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.beehive.ApiaryTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class UnvalidatedApiaryContainer extends Container {

    public ApiaryTileEntity apiaryTileEntity;
    public BlockPos pos;

    public UnvalidatedApiaryContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.UNVALIDATED_APIARY_CONTAINER.get(), id);

        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity)world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }


}
