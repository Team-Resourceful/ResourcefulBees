package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.BreederBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BreederBlock extends BeeHouseBlock {

    public BreederBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BreederBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : RenderingBaseEntityBlock.createTickerHelper(type, ModBlockEntityTypes.BREEDER_BLOCK_ENTITY.get(), BreederBlockEntity::serverTick);
    }
}
