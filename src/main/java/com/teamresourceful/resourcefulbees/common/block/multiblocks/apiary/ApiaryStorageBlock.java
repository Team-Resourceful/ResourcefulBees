package com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.block.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ApiaryStorageBlock extends RenderingBaseEntityBlock {
    public ApiaryStorageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockRayTraceResult) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            MenuProvider blockEntity = state.getMenuProvider(world,pos);
            NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
        return (MenuProvider)worldIn.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ApiaryStorageTileEntity(pos, state);
    }
}
