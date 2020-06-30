package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

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

  /**
   * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
   * this method is unrelated to {@link } and {@link #}, and will always be called regardless
   * of whether the block can receive random update ticks
   */
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
    if (stateIn.get(VALIDATED)) {
      for(int i = 0; i < rand.nextInt(1) + 1; ++i) {
        this.createHoneyEffect(worldIn, pos, stateIn);
      }
    }

  }

  public static void dropResourceHoneycomb(ApiaryBlock block, World world, BlockPos pos) {
    block.dropResourceHoneycomb(world, pos);
  }

  public void dropResourceHoneycomb(World world, BlockPos pos) {
    TileEntity blockEntity = world.getTileEntity(pos);
    if (blockEntity instanceof ApiaryTileEntity) {
      ApiaryTileEntity hive = (ApiaryTileEntity)blockEntity;
      while (hive.hasCombs()) {
        ItemStack comb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        String honeycomb = hive.getResourceHoneycomb();
        comb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, BeeInfo.getInfo(honeycomb).getHoneycombColor());
        comb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, BeeInfo.getInfo(honeycomb).getName());
        spawnAsEntity(world, pos, comb);
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void createHoneyEffect(World world, BlockPos pos, BlockState state) {
    if (state.getFluidState().isEmpty() && !(world.rand.nextFloat() < 0.3F)) {
      VoxelShape voxelshape = state.getCollisionShape(world, pos);
      double d0 = voxelshape.getEnd(Direction.Axis.Y);
      if (d0 >= 1.0D && !state.isIn(BlockTags.IMPERMEABLE)) {
        double d1 = voxelshape.getStart(Direction.Axis.Y);
        if (d1 > 0.0D) {
          this.addHoneyParticle(world, pos, voxelshape, (double)pos.getY() + d1 - 0.05D);
        } else {
          BlockPos blockpos = pos.down();
          BlockState blockstate = world.getBlockState(blockpos);
          VoxelShape voxelshape1 = blockstate.getCollisionShape(world, blockpos);
          double d2 = voxelshape1.getEnd(Direction.Axis.Y);
          if ((d2 < 1.0D || !blockstate.isCollisionShapeOpaque(world, blockpos)) && blockstate.getFluidState().isEmpty()) {
            this.addHoneyParticle(world, pos, voxelshape, (double)pos.getY() - 0.05D);
          }
        }
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void addHoneyParticle(World p_226880_1_, BlockPos p_226880_2_, VoxelShape p_226880_3_, double p_226880_4_) {
    this.addHoneyParticle(p_226880_1_, (double)p_226880_2_.getX() + p_226880_3_.getStart(Direction.Axis.X), (double)p_226880_2_.getX() + p_226880_3_.getEnd(Direction.Axis.X), (double)p_226880_2_.getZ() + p_226880_3_.getStart(Direction.Axis.Z), (double)p_226880_2_.getZ() + p_226880_3_.getEnd(Direction.Axis.Z), p_226880_4_);
  }

  @OnlyIn(Dist.CLIENT)
  private void addHoneyParticle(World p_226875_1_, double p_226875_2_, double p_226875_4_, double p_226875_6_, double p_226875_8_, double p_226875_10_) {
    p_226875_1_.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(p_226875_1_.rand.nextDouble(), p_226875_2_, p_226875_4_), p_226875_10_, MathHelper.lerp(p_226875_1_.rand.nextDouble(), p_226875_6_, p_226875_8_), 0.0D, 0.0D, 0.0D);
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

  /**
   * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
   * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
   * @deprecated call via {@link #()} whenever possible. Implementing/overriding is fine.
   */
  @Nonnull
  public BlockRenderType getRenderType(@Nonnull BlockState state) {
    return BlockRenderType.MODEL;
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
