package com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.block.RenderingBaseEntityBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
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
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBlock extends RenderingBaseEntityBlock {

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty VALIDATED = BooleanProperty.create("validated");

  private final int tier;

  public ApiaryBlock(final int tier, float hardness, float resistance) {
    super(BlockBehaviour.Properties.of(Material.METAL).strength(hardness, resistance).sound(SoundType.METAL));
    this.tier = tier;
    this.registerDefaultState(this.stateDefinition.any().setValue(VALIDATED, false).setValue(FACING, Direction.NORTH));
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
    builder.add(VALIDATED, FACING);
  }

  @Nullable
  @Override
  public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
    return (MenuProvider)worldIn.getBlockEntity(pos);
  }

  @Override
  public void setPlacedBy(Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
    BlockEntity tile = worldIn.getBlockEntity(pos);
    if(tile instanceof ApiaryTileEntity apiaryTileEntity) {
      apiaryTileEntity.setTier(tier);
    }
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

      if (tier >= 0) {
        int timeReduction = (int)((0.1 + (tier * .1)) * 100);
        tooltip.add(new TranslatableComponent(TranslationConstants.BeeHive.HIVE_TIME, "-", timeReduction).withStyle(ChatFormatting.GOLD));
      }
      ApiaryOutputType outputTypeEnum;
      int outputQuantity;

      switch (tier) {
        case 8 -> {
          outputTypeEnum = CommonConfig.T4_APIARY_OUTPUT.get();
          outputQuantity = CommonConfig.T4_APIARY_QUANTITY.get();
        }
        case 7 -> {
          outputTypeEnum = CommonConfig.T3_APIARY_OUTPUT.get();
          outputQuantity = CommonConfig.T3_APIARY_QUANTITY.get();
        }
        case 6 -> {
          outputTypeEnum = CommonConfig.T2_APIARY_OUTPUT.get();
          outputQuantity = CommonConfig.T2_APIARY_QUANTITY.get();
        }
        default -> {
          outputTypeEnum = CommonConfig.T1_APIARY_OUTPUT.get();
          outputQuantity = CommonConfig.T1_APIARY_QUANTITY.get();
        }
      }

      TranslatableComponent outputType = outputTypeEnum.equals(ApiaryOutputType.COMB) ? TranslationConstants.Apiary.HONEYCOMB : TranslationConstants.Apiary.HONEYCOMB_BLOCK;

      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_TYPE, outputType).withStyle(ChatFormatting.GOLD));
      tooltip.add(new TranslatableComponent(TranslationConstants.Apiary.OUTPUT_QUANTITY, outputQuantity).withStyle(ChatFormatting.GOLD));
    }
    else if (Screen.hasControlDown()){
      tooltip.add(TranslationConstants.Apiary.STRUCTURE_SIZE.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.REQUISITES.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.DROPS.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.TAGS.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.OFFSET.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.LOCK.withStyle(ChatFormatting.AQUA));
      tooltip.add(TranslationConstants.Apiary.LOCK2.withStyle(ChatFormatting.AQUA));
    } else {
      tooltip.add(TranslationConstants.Items.MORE_INFO.withStyle(ChatFormatting.YELLOW));
      tooltip.add(TranslationConstants.Items.MULTIBLOCK_INFO.withStyle(ChatFormatting.AQUA));
    }

    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new ApiaryTileEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
    return level.isClientSide ?
            null :
            createTickerHelper(type, ModBlockEntityTypes.APIARY_TILE_ENTITY.get(), ApiaryTileEntity::serverTick);
  }
}
