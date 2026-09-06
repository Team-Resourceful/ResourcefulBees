package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blockentities.FlowHiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHolderBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.lib.util.FluidUtils;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class FlowHiveBlock extends BeeHouseBlock implements BeeHolderBlock {

    private static final MapCodec<FlowHiveBlock> CODEC = BlockBehaviour.simpleCodec(FlowHiveBlock::new);

    public FlowHiveBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FlowHiveBlockEntity flowHive) {
            boolean moved = FluidUtil.interactWithFluidHandler(player, hand, pos, flowHive.fluidHandler(), null);
            if (moved) {
                return InteractionResult.SUCCESS_SERVER;
            }
            return FluidUtils.fillOrEmptyBottle(flowHive.fluidHandler(), player, hand);
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FlowHiveBlockEntity(pos, state);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), BeeHolderBlockEntity::serverTick);
    }
}
