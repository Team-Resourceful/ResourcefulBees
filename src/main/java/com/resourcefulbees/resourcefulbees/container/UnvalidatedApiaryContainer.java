package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class UnvalidatedApiaryContainer extends Container {

    private final ApiaryTileEntity apiaryTileEntity;
    private final BlockPos pos;

    public UnvalidatedApiaryContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), id);
        this.pos = pos;
        this.apiaryTileEntity =(ApiaryTileEntity)world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }


    public ApiaryTileEntity getApiaryTileEntity() {
        return apiaryTileEntity;
    }

    public BlockPos getPos() {
        return pos;
    }
}
