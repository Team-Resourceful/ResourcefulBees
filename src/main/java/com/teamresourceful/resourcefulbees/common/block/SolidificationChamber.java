package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.tileentity.SolidificationChamberTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings("deprecation")
public class SolidificationChamber extends AbstractTank {

    protected static final VoxelShape VOXEL_SHAPE = Util.make(() -> {
        VoxelShape shape = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 5.0D, 15.0D);
        shape = VoxelShapes.join(shape, Block.box(10.0D, 5.0D, 1.0D, 6.0D, 9.0D, 15.0D), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, Block.box(1.0D, 5.0D, 10.0D, 15.0D, 9.0D, 6.0D), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, Block.box(3.0D, 5.0D, 3.0D, 13.0D, 16.0D, 13.0D), IBooleanFunction.OR);
        return shape;
    });

    public SolidificationChamber(Properties properties) {
        super(properties);
    }

    private static SolidificationChamberTileEntity getTileEntity(@NotNull IBlockReader world, @NotNull BlockPos pos) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof SolidificationChamberTileEntity) {
            return (SolidificationChamberTileEntity) entity;
        }
        return null;
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult blockRayTraceResult) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof SolidificationChamberTileEntity) {
            if (!world.isClientSide) {
                FluidTank tank = ((SolidificationChamberTileEntity) tileEntity).getTank();
                Item item = player.getItemInHand(hand).getItem();
                if (item instanceof HoneyBottleItem) {
                    HoneyFluidTank.emptyBottle(tank, player, hand);
                } else if (item instanceof GlassBottleItem) {
                    HoneyFluidTank.fillBottle(tank, player, hand);
                } else {
                    capabilityOrGuiUse(tileEntity, player, world, pos, hand);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull World world, @NotNull BlockPos pos, @NotNull Random rand) {
        SolidificationChamberTileEntity tank = getTileEntity(world, pos);
        if (tank == null) {
            return;
        }
        if (tank.getTank().getFluid().getFluid() instanceof CustomHoneyFluid) {
            CustomHoneyFluid fluid = (CustomHoneyFluid) tank.getTank().getFluid().getFluid();
            if (fluid.getHoneyData().getColor().isRainbow()) {
                world.sendBlockUpdated(pos, stateIn, stateIn, 2);
            }
        }
        super.animateTick(stateIn, world, pos, rand);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SolidificationChamberTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return VOXEL_SHAPE;
    }
}
