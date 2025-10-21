package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicBoolean;

public record BlockBonemealedEvent(Player player, Level level, BlockPos pos, BlockState state, ItemStack stack, AtomicBoolean canceled) {

    public static final EventHelper<BlockBonemealedEvent> EVENT = new EventHelper<>();

    public boolean isCanceled() {
        return canceled.get();
    }

    public void setCanceled(boolean canceled) {
        this.canceled.set(canceled);
    }
}
