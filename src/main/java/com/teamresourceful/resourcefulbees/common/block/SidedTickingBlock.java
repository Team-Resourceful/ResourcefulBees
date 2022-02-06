package com.teamresourceful.resourcefulbees.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SidedTickingBlock<E extends BlockEntity> extends RenderingBaseEntityBlock {

    private final RegistryObject<BlockEntityType<E>> blockEntity;
    private final BlockEntityTicker<E> serverTick;
    private final BlockEntityTicker<E> clientTick;

    protected SidedTickingBlock(RegistryObject<BlockEntityType<E>> blockEntity, BlockEntityTicker<E> serverTick, BlockEntityTicker<E> clientTick, Properties properties) {
        super(properties);
        this.blockEntity = blockEntity;
        this.serverTick = serverTick;
        this.clientTick = clientTick;
    }

    public static <E extends BlockEntity> SidedTickingBlock<E> server(RegistryObject<BlockEntityType<E>> blockEntity, BlockEntityTicker<E> serverTick, Properties properties){
        return both(blockEntity, serverTick, null, properties);
    }

    public static <E extends BlockEntity> SidedTickingBlock<E> both(RegistryObject<BlockEntityType<E>> blockEntity, BlockEntityTicker<E> serverTick, BlockEntityTicker<E> clientTick, Properties properties){
        return new SidedTickingBlock<>(blockEntity, serverTick, clientTick, properties);
    }

    public static <E extends BlockEntity> SidedTickingBlock<E> client(RegistryObject<BlockEntityType<E>> blockEntity, BlockEntityTicker<E> clientTick, Properties properties){
        return both(blockEntity, null, clientTick, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return blockEntity.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) return createTickerHelper(type, blockEntity.get(), clientTick);
        return createTickerHelper(type, blockEntity.get(), serverTick);
    }
}
