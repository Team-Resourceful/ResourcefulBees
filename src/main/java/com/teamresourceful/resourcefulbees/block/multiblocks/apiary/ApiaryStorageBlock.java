package com.teamresourceful.resourcefulbees.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ApiaryStorageBlock extends Block {
    public ApiaryStorageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            INamedContainerProvider blockEntity = state.getMenuProvider(world,pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos) {
        return (INamedContainerProvider)worldIn.getBlockEntity(pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ApiaryStorageTileEntity();
    }
}
