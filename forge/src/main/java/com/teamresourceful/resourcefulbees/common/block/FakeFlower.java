package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blockentity.FakeFlowerEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.item.HoneyDipperItem;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FakeFlower extends RenderingBaseEntityBlock {

    private static final VoxelShape FULL_SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public FakeFlower(Properties arg) {
        super(arg);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return FULL_SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FakeFlowerEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (HoneyDipperItem.isHoldingHoneyDipper(player)) {
            return InteractionResult.FAIL;
        } else if (level.getBlockEntity(pos) instanceof FakeFlowerEntity fakeFlower) {
            if (!level.isClientSide) {
                ModUtils.capabilityOrGuiUse(fakeFlower, player, level, pos, hand);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
