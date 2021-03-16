package com.resourcefulbees.resourcefulbees.block.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
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

import net.minecraft.block.AbstractBlock.Properties;

@SuppressWarnings("deprecation")
public class ApiaryStorageBlock extends Block {
    public ApiaryStorageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            INamedContainerProvider blockEntity = state.getMenuProvider(world,pos);
            if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
            }
        }
        return super.use(state, world, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
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
