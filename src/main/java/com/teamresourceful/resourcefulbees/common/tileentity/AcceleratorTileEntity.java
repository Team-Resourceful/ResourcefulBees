package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.registry.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AcceleratorTileEntity extends TileEntity implements ITickableTileEntity {
    public AcceleratorTileEntity() {
        super(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        assert level != null : "World is null?!?!?";
        accelerateTick(level, worldPosition.below());
        accelerateTick(level, worldPosition.above());
        accelerateTick(level, worldPosition.north());
        accelerateTick(level, worldPosition.south());
        accelerateTick(level, worldPosition.east());
        accelerateTick(level, worldPosition.west());
    }

    public static void accelerateTick(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if(!world.isClientSide && world instanceof ServerWorld && blockState.isRandomlyTicking() && world.getRandom().nextInt(40) == 0) {
            blockState.randomTick((ServerWorld) world, pos, world.getRandom());
        }
        if (blockState.hasTileEntity()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null && !tileEntity.isRemoved() && tileEntity instanceof ITickableTileEntity && !(tileEntity instanceof AcceleratorTileEntity)) {
                for (int i = 0; i < 384; i++) {
                    ((ITickableTileEntity) tileEntity).tick();
                }
            }
        }
    }
}
