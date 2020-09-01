package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.tileentity.ApiaryStorageTileEntity;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ApiaryStorageBlock extends Block {
    public ApiaryStorageBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState());
    }

    @Nonnull
    public ActionResultType onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        if (!world.isRemote) {
            INamedContainerProvider blockEntity = state.getContainer(world,pos);
            if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        return (INamedContainerProvider)worldIn.getTileEntity(pos);
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
