package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AcceleratorTileEntity extends TileEntity implements ITickableTileEntity {
    public AcceleratorTileEntity() {
        super(ModTileEntityTypes.ACCELERATOR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        assert world != null : "World is null?!?!?";
        accelerateTick(world, pos.down());
        accelerateTick(world, pos.up());
        accelerateTick(world, pos.north());
        accelerateTick(world, pos.south());
        accelerateTick(world, pos.east());
        accelerateTick(world, pos.west());
    }

    public static void accelerateTick(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(!world.isRemote && world instanceof ServerWorld){
            if (block.ticksRandomly(blockState) && world.getRandom().nextInt(40) == 0) {
                block.randomTick(blockState, (ServerWorld) world, pos, world.getRandom());
            }
        }
        if (block.hasTileEntity(blockState)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null && !tileEntity.isRemoved() && tileEntity instanceof ITickableTileEntity && !(tileEntity instanceof AcceleratorTileEntity)) {
                for (int i = 0; i < 384; i++) {
                    ((ITickableTileEntity) tileEntity).tick();
                }
            }
        }
    }
}
