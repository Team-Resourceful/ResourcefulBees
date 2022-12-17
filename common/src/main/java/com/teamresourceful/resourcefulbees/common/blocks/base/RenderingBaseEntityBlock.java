package com.teamresourceful.resourcefulbees.common.blocks.base;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class RenderingBaseEntityBlock extends BaseEntityBlock {
    protected RenderingBaseEntityBlock(Properties properties) {
        super(properties);
    }

    /*
        DONT KNOW WHY VANILLA DEFAULTS TO INVISIBLE BUT SCREW THAT.
     */
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeIn, BlockEntityType<E> typeCheck, BlockEntityTickerSingleton<? super E> ticker) {
        //noinspection unchecked
        return typeIn == typeCheck ? (pLevel, pPos, pState, pBlockEntity) -> ticker.tick((E) pBlockEntity) : null;
    }

    @FunctionalInterface
    public interface BlockEntityTickerSingleton<T extends BlockEntity> {
        void tick(T pBlockEntity);
    }
}
