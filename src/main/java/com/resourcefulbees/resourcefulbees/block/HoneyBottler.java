package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyBottlerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Random;

public class HoneyBottler extends Block {

    protected static final VoxelShape VOXEL_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public HoneyBottler(Properties properties) {
        super(properties);
    }

    private static HoneyBottlerTileEntity getTileEntity(@Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof HoneyBottlerTileEntity) {
            return (HoneyBottlerTileEntity) entity;
        }
        return null;
    }

    @Override
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();

        if (tileEntity instanceof HoneyBottlerTileEntity) {
            HoneyBottlerTileEntity bottler = (HoneyBottlerTileEntity) tileEntity;
            if (!world.isClientSide) {
                if (hasCapability) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                            .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                } else {
                    NetworkHooks.openGui((ServerPlayerEntity) player, bottler, pos);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        HoneyBottlerTileEntity tank = getTileEntity(world, pos);
        if (tank == null) {
            return;
        }
        if (tank.getFluidTank().getFluid().getFluid() instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid fluid = (HoneyFlowingFluid) tank.getFluidTank().getFluid().getFluid();
            if (fluid.getHoneyData().isRainbow()) {
                world.sendBlockUpdated(pos, stateIn, stateIn, 2);
            }
        }
        super.animateTick(stateIn, world, pos, rand);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HoneyBottlerTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return VOXEL_SHAPE;
    }
}
