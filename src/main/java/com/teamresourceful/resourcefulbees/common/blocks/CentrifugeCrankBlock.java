package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeCrankBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CentrifugeCrankBlock extends RenderingBaseEntityBlock {

    private static final MapCodec<CentrifugeBlock> CODEC = simpleCodec(CentrifugeBlock::new);

    public static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);

    public CentrifugeCrankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        BlockState stateBelow = level.getBlockState(pos.below());
        BlockEntity be = level.getBlockEntity(pos.below());
        if (stateBelow.hasProperty(CentrifugeBlock.USABLE) && stateBelow.getValue(CentrifugeBlock.USABLE)) {
            if (be instanceof CentrifugeBlockEntity centrifuge && !level.isClientSide()) {
                centrifuge.use();
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CentrifugeCrankBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(@NonNull BlockState state, Level level, BlockPos pos, @NonNull Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        if (!(level.getBlockState(pos.below()).getBlock() instanceof CentrifugeBlock))
            level.destroyBlock(pos, true);
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }
}
