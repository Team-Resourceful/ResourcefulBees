package com.teamresourceful.resourcefulbees.common.blocks.base;

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public interface MenuBlock {

    default @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, Player player, @NonNull BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() && !level.isClientSide()) {
            MenuProvider blockEntity = state.getMenuProvider(level,pos);
            if (blockEntity instanceof ContentMenuProvider<?> contentMenu) {
                contentMenu.openMenu((ServerPlayer) player);
            } else if (blockEntity != null) {
                player.openMenu(blockEntity);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
