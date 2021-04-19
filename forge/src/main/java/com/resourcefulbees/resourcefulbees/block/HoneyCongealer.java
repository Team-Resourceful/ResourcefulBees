package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyCongealerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

import java.util.Random;

@SuppressWarnings("deprecation")
public class HoneyCongealer extends Block {

    protected static final VoxelShape VOXEL_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public HoneyCongealer(Properties properties) {
        super(properties);
    }

    private static HoneyCongealerTileEntity getTileEntity(@NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof HoneyCongealerTileEntity) {
            return (HoneyCongealerTileEntity) entity;
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockRayTraceResult) {
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof HoneyCongealerTileEntity) {
            if (!world.isClientSide) {
                CentrifugeBlock.capabilityOrGuiUse(tileEntity, player, world, pos, hand);
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull Random rand) {
        HoneyCongealerTileEntity tank = getTileEntity(world, pos);
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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new HoneyCongealerTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE;
    }
}
