package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.lib.enums.HoneyPotState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyPotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotBlock extends AbstractTank {

    private static final VoxelShape NO_LID = Util.make(() -> {
        VoxelShape shape = VoxelShapes.box(0.125, 0, 0.125, 0.875, 0.625, 0.875);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.625, 0.3125, 0.6875, 0.6875, 0.6875), IBooleanFunction.OR);
        return shape;
    });

    private static final VoxelShape LID = Util.make(() -> {
        VoxelShape shape = NO_LID;
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.6875, 0.1875, 0.8125, 0.8125, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.4375, 0.8125, 0.4375, 0.5625, 0.9375, 0.5625), IBooleanFunction.OR);
        return shape;
    });

    public static final EnumProperty<HoneyPotState> LID_STATE = EnumProperty.create("lid", HoneyPotState.class);

    public HoneyPotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any().setValue(LID_STATE, HoneyPotState.CLOSED));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LID_STATE);
    }

    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult result) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof HoneyPotTileEntity) {
            Item item = player.getItemInHand(hand).getItem();
            HoneyPotTileEntity.HoneyPotFluidTank tank = ((HoneyPotTileEntity) tileEntity).getTank();

            if (item instanceof GlassBottleItem) tank.fillBottle(player, hand);
            else if (item instanceof HoneyBottleItem) tank.emptyBottle(player, hand);
            else capabilityOrGuiUse(tileEntity, player, world, pos, hand);

            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, result);
    }


    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull World level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        if (pos.above().equals(fromPos)) {
            TileEntity tileAbove = level.getBlockEntity(pos.above());
            HoneyPotState potState = HoneyPotState.CLOSED;

            if (level.getBlockState(pos.above()).getBlock().equals(ModBlocks.ENDER_BEECON.get())) potState = HoneyPotState.BEECON;
            else if (tileAbove != null && tileAbove.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent()) potState = HoneyPotState.OPEN;

            level.setBlock(pos, state.setValue(LID_STATE, potState), Constants.BlockFlags.BLOCK_UPDATE);

        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HoneyPotTileEntity();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader level, @NotNull BlockPos pos, @NotNull ISelectionContext ctx) {
        return state.getValue(LID_STATE).equals(HoneyPotState.CLOSED) ? LID : NO_LID;
    }
}
