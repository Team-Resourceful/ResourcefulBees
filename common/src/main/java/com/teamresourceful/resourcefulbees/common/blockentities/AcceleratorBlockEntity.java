package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class AcceleratorBlockEntity extends BlockEntity implements InstanceBlockEntityTicker {

    public AcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ACCELERATOR_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        accelerateTick(level, pos.below());
        accelerateTick(level, pos.above());
        accelerateTick(level, pos.north());
        accelerateTick(level, pos.south());
        accelerateTick(level, pos.east());
        accelerateTick(level, pos.west());
    }

    private static void accelerateTick(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if(!level.isClientSide && level instanceof ServerLevel serverLevel && state.isRandomlyTicking() && level.getRandom().nextInt(40) == 0) {
            state.randomTick(serverLevel, pos, level.getRandom());
        }
        tickBlock(level, pos, state, level.getBlockEntity(pos));
    }

    private static <T extends BlockEntity> void tickBlock(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity == null) return;
        try {
            //noinspection unchecked
            BlockEntityTicker<T> ticker = (BlockEntityTicker<T>) state.getTicker(level, blockEntity.getType());
            if (!blockEntity.isRemoved() && ticker != null && !(blockEntity instanceof AcceleratorBlockEntity)) {
                for (int i = 0; i < 384; i++) {
                    ticker.tick(level, pos, state, blockEntity);
                }
            }
        }catch (Exception e) {
            //DO NOTHING, This should never happen.
        }
    }
}
