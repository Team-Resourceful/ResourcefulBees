package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyPotBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.lib.enums.HoneyPotState;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class HoneyPotBlock extends RenderingBaseEntityBlock implements MenuBlock {

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

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        if (itemStack.is(ModItems.HONEY_DIPPER.get())) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(LID_STATE, state.getValue(LID_STATE).toggle()), Block.UPDATE_ALL);
            }

            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HoneyPotBlockEntity honeyPot) {
            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, honeyPot.tank(), null);

            if (moved) {
                return InteractionResult.SUCCESS_SERVER;
            }

            return FluidUtils.fillOrEmptyBottle(honeyPot.tank(), player, hand);
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
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

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
