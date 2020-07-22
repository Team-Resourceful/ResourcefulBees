package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ApiaryBlock extends Block {

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static final BooleanProperty VALIDATED = BooleanProperty.create("validated");


  public ApiaryBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.stateContainer.getBaseState().with(VALIDATED, false).with(FACING, Direction.NORTH));
  }

  @Nonnull
  public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
    if (!world.isRemote) {
      INamedContainerProvider blockEntity = state.getContainer(world,pos);
      if (blockEntity != null) {
        NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
      }
    }
    return ActionResultType.SUCCESS;
  }

  public BlockState getStateForPlacement(BlockItemUseContext context) {
    if (context.getPlayer() != null && context.getPlayer().isSneaking()) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(VALIDATED, FACING);
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
    return new ApiaryTileEntity();
  }

}
