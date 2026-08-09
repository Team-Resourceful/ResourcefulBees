package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.MenuBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.RenderingBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CentrifugeBlock extends RenderingBaseEntityBlock implements MenuBlock {

    public static final VoxelShape SHAPE = Shapes.join(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D),
            BooleanOp.OR
    );

    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 1, 8);
    public static final BooleanProperty USABLE = BooleanProperty.create("usable");

    public static final MapCodec<CentrifugeCrankBlock> CODEC = simpleCodec(CentrifugeCrankBlock::new);

    public CentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(ROTATION, 1).setValue(USABLE, false));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        try (Transaction transaction = Transaction.openRoot()) {
            var moved = FluidUtil.interactWithFluidHandler(player, hand, level, pos, null, transaction);
            if (moved) {
                transaction.commit();
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

//    @Override
//    protected InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
//        if (level.getBlockEntity(pos) instanceof CentrifugeBlockEntity centrifuge) {
//            if (!level.isClientSide()) {
//                //FluidUtils.checkBottleAndCapability(centrifuge.getFluidContainer(), centrifuge, player, level, pos, hand);
//            }
//            return InteractionResult.SUCCESS_SERVER;
//        }
//
//        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
//    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION).add(USABLE);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CentrifugeBlockEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, Player player, @NonNull BlockHitResult hitResult) {
        return MenuBlock.super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
