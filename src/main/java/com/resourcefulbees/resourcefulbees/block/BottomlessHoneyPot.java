package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.tileentity.BottomlessHoneyPotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BottomlessHoneyPot extends Block {

    protected static final VoxelShape VOXEL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private static final DirectionProperty FACING = HorizontalBlock.FACING;

    public BottomlessHoneyPot(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BottomlessHoneyPotTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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

    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult blockRayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean usingHoney = heldItem.getItem() instanceof HoneyBottleItem;
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof BottomlessHoneyPotTileEntity) {
            BottomlessHoneyPotTileEntity tank = (BottomlessHoneyPotTileEntity) tileEntity;
            if (!world.isClientSide && usingHoney) {
                tank.emptyBottle(player, hand);
                return ActionResultType.SUCCESS;
            } else if (player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                        .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }
}
