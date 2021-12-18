package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blockentity.CreativeGenBlockEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CreativeGen extends RenderingBaseEntityBlock {
    public CreativeGen(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CreativeGenBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ?
                null :
                createTickerHelper(type, ModBlockEntityTypes.CREATIVE_GEN_ENTITY.get(), CreativeGenBlockEntity::serverTick);
    }
}
