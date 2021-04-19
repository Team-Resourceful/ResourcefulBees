package com.resourcefulbees.resourcefulbees.block.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBlock extends Block {

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

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
    return new ApiaryTileEntity();
  }

  @Nullable
  @Override
  public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
    return (MenuProvider)worldIn.getBlockEntity(pos);
  }

  @Override
  public void setPlacedBy(Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
    BlockEntity tile = worldIn.getBlockEntity(pos);
    if(tile instanceof ApiaryTileEntity) {
      ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tile;
      apiaryTileEntity.setTier(tier);
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
    if(Screen.hasShiftDown())
    {
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.get("block.resourcefulbees.beehive.tooltip.max_bees"))
              .appendText(" " + Config.APIARY_MAX_BEES.get())
              .appendText(" " + I18n.get("block.resourcefulbees.beehive.tooltip.unique_bees"), ChatFormatting.BOLD)
              .appendText( ChatFormatting.GOLD + " Bees", ChatFormatting.RESET)
              .applyStyle(ChatFormatting.GOLD)
              .build());
      if (tier != 1) {
        int timeReduction = (int)((0.1 + (tier * .05)) * 100);
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.get("block.resourcefulbees.beehive.tooltip.hive_time"))
                .appendText(" -" + timeReduction + "%")
                .applyStyle(ChatFormatting.GOLD)
                .build());
      }
      ApiaryOutput outputTypeEnum;
      int outputQuantity;

      switch (tier) {
        case 8:
          outputTypeEnum = Config.T4_APIARY_OUTPUT.get();
          outputQuantity = Config.T4_APIARY_QUANTITY.get();
          break;
        case 7:
          outputTypeEnum = Config.T3_APIARY_OUTPUT.get();
          outputQuantity = Config.T3_APIARY_QUANTITY.get();
          break;
        case 6:
          outputTypeEnum = Config.T2_APIARY_OUTPUT.get();
          outputQuantity = Config.T2_APIARY_QUANTITY.get();
          break;
        default:
          outputTypeEnum = Config.T1_APIARY_OUTPUT.get();
          outputQuantity = Config.T1_APIARY_QUANTITY.get();
      }

      String outputType = outputTypeEnum.equals(ApiaryOutput.COMB) ? I18n.get("honeycomb.resourcefulbees") : I18n.get("honeycomb_block.resourcefulbees");

      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.output_type"))
              .appendText(" " + outputType)
              .applyStyle(ChatFormatting.GOLD)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.output_quantity"))
              .appendText(" " + outputQuantity)
              .applyStyle(ChatFormatting.GOLD)
              .build());
    }
    else if (Screen.hasControlDown()){
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.structure_size"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.requisites"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.drops"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.tags"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.offset"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.lock"), ChatFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.lock_2"), ChatFormatting.AQUA)
              .build());
    }
    else
    {
      tooltip.add(new TextComponent(ChatFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));
      tooltip.add(new TextComponent(ChatFormatting.AQUA + I18n.get("resourcefulbees.ctrl_info")));
    }

    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
