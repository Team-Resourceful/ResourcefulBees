package com.teamresourceful.resourcefulbees.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public interface InstanceBlockEntityTicker {

    Side getSide();

    default void serverTick(Level level, BlockPos pos, BlockState state) {
        if (getSide() == Side.SERVER || getSide() == Side.BOTH) {
            throw new IllegalArgumentException("Ticking block entity serverTick method override expected.");
        }
    }

    default void clientTick(Level level, BlockPos pos, BlockState state) {
        if (getSide() == Side.CLIENT || getSide() == Side.BOTH) {
            throw new IllegalArgumentException("Ticking block entity clientTick method override expected.");
        }
    }

    static <T extends BlockEntity & InstanceBlockEntityTicker> BlockEntityTicker<T> createTicker() {
        return (level, pos, state, entity) -> {
            if (!level.isClientSide() && entity.getSide() != Side.CLIENT) {
                entity.serverTick(level, pos, state);
            }
            if (level.isClientSide() && entity.getSide() != Side.SERVER) {
                entity.clientTick(level, pos, state);
            }
        };
    }

    enum Side {
        SERVER,
        CLIENT,
        BOTH
    }
}
