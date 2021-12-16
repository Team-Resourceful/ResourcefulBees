package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ApiaryBlock extends BeeHouseBlock {

  public static final VoxelShape FULL_Z_SHAPE = Stream.of(
          Block.box(1, 0, 1, 15, 13, 15),
          Block.box(0, 13, 0, 16, 16, 16),
          Block.box(1, 16, 0, 15, 18, 16),
          Block.box(3, 18, 0, 13, 20, 16),
          Block.box(5, 20, 0, 11, 22, 16),
          Block.box(7, 22, -1, 9, 24, 17)
  ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

  public static final VoxelShape FULL_X_SHAPE = Stream.of(
          Block.box(0, 16, 1, 16, 18, 15),
          Block.box(0, 13, 0, 16, 16, 16),
          Block.box(1, 0, 1, 15, 13, 15),
          Block.box(0, 18, 3, 16, 20, 13),
          Block.box(0, 20, 5, 16, 22, 11),
          Block.box(-1, 22, 7, 17, 24, 9)
  ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  private final ApiaryTier tier;

  public ApiaryBlock(final ApiaryTier tier, float hardness, float resistance) {
    super(BlockBehaviour.Properties.of(Material.METAL).strength(hardness, resistance).sound(SoundType.METAL));
    this.tier = tier;
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
    if (!player.isShiftKeyDown() && !world.isClientSide) {
      MenuProvider blockEntity = state.getMenuProvider(world,pos);
      NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
    return (MenuProvider)worldIn.getBlockEntity(pos);
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
    if(Screen.hasShiftDown())
    {
      tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.MAX_BEES, CommonConfig.APIARY_MAX_BEES.get())
              .append(TranslationConstants.BeeHive.UNIQUE.withStyle(ChatFormatting.BOLD))
              .withStyle(ChatFormatting.GOLD)
      );

      int timeReduction = 100 - (int)(tier.getTimeModifier() * 100);
      tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.HIVE_TIME, "-", timeReduction).withStyle(ChatFormatting.GOLD));
      TranslatableComponent outputType = tier.getOutputType().equals(ApiaryOutputType.COMB) ? TranslationConstants.Apiary.HONEYCOMB : TranslationConstants.Apiary.HONEYCOMB_BLOCK;

      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_TYPE, outputType).withStyle(ChatFormatting.GOLD));
      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_QUANTITY, tier.getOutputAmount()).withStyle(ChatFormatting.GOLD));
    } else {
      tooltip.add(TranslationConstants.Items.MORE_INFO.withStyle(ChatFormatting.YELLOW));
    }

    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }

  @NotNull
  @Override
  public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return state.hasProperty(FACING) && state.getValue(FACING).getAxis().equals(Direction.Axis.Z) ? FULL_Z_SHAPE : FULL_X_SHAPE;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryTileEntity(tier, pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide ?
            null :
            createTickerHelper(type, tier.getBlockEntityType(), ApiaryTileEntity::serverTick);
  }
}
