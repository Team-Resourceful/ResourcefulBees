package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AcceleratorTileEntity extends BlockEntity implements TickableBlockEntity {
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

    public static void accelerateTick(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if(!world.isClientSide && world instanceof ServerLevel && blockState.isRandomlyTicking() && world.getRandom().nextInt(40) == 0) {
            blockState.randomTick((ServerLevel) world, pos, world.getRandom());
        }
        if (blockState.hasTileEntity()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null && !tileEntity.isRemoved() && tileEntity instanceof TickableBlockEntity && !(tileEntity instanceof AcceleratorTileEntity)) {
                for (int i = 0; i < 384; i++) {
                    ((TickableBlockEntity) tileEntity).tick();
                }
            }
        }
    }
}
