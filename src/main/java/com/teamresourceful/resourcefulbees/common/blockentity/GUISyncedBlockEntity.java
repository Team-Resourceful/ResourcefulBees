package com.teamresourceful.resourcefulbees.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class GUISyncedBlockEntity extends BlockEntity implements ISyncableGUI {

    private final List<ServerPlayer> listeners = new ArrayList<>();

    protected GUISyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public final List<ServerPlayer> getListeners() {
        return listeners;
    }
}
