package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.lib.enums.HoneyPotState;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.util.FluidUtils;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotBlock extends RenderingBaseEntityBlock {

    private static final VoxelShape NO_LID = Util.make(() -> {
        VoxelShape shape = Shapes.box(0.125, 0, 0.125, 0.875, 0.625, 0.875);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.625, 0.3125, 0.6875, 0.6875, 0.6875), BooleanOp.OR);
        return shape;
    });

    private static final VoxelShape LID = Util.make(() -> {
        VoxelShape shape = NO_LID;
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.6875, 0.1875, 0.8125, 0.8125, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.8125, 0.4375, 0.5625, 0.9375, 0.5625), BooleanOp.OR);
        return shape;
    });

    public static final EnumProperty<HoneyPotState> LID_STATE = EnumProperty.create("lid", HoneyPotState.class);

    public HoneyPotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(LID_STATE, HoneyPotState.CLOSED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LID_STATE);
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        if (level.getBlockEntity(pos) instanceof HoneyPotBlockEntity pot) {
            if (!level.isClientSide) {
                FluidUtils.checkBottleAndCapability(pot.getFluidContainer(), pot, player, level, pos, hand);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        if (pos.above().equals(fromPos)) {
            BlockEntity blockEntityAbove = level.getBlockEntity(pos.above());
            HoneyPotState potState = HoneyPotState.CLOSED;

            if (level.getBlockState(pos.above()).getBlock().equals(ModBlocks.ENDER_BEECON.get())) {
                potState = HoneyPotState.BEECON;
            } else if (blockEntityAbove != null && FluidContainer.holdsFluid(blockEntityAbove, null)) {
                potState = HoneyPotState.OPEN;
            }

            level.setBlock(pos, state.setValue(LID_STATE, potState), Block.UPDATE_ALL); //TODO determine if this is the right flag to use might just need UPDATE_CLIENTS
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new HoneyPotBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        return state.getValue(LID_STATE).equals(HoneyPotState.CLOSED) ? LID : NO_LID;
    }
}
