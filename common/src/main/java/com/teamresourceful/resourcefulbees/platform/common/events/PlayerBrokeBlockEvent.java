package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record PlayerBrokeBlockEvent(Level level, Player player, BlockPos pos, BlockState state) {

    public static final EventHelper<PlayerBrokeBlockEvent> EVENT = new EventHelper<>();
}
