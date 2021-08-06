package com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.TooltipBuilder;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBlock extends Block {

  public static final DirectionProperty FACING = HorizontalBlock.FACING;
  public static final BooleanProperty VALIDATED = BooleanProperty.create("validated");

  private final int tier;

  public ApiaryBlock(final int tier, float hardness, float resistance) {
    super(AbstractBlock.Properties.of(Material.METAL).strength(hardness, resistance).sound(SoundType.METAL));
    this.tier = tier;
    this.registerDefaultState(this.stateDefinition.any().setValue(VALIDATED, false).setValue(FACING, Direction.NORTH));
  }

  @Override
  public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
    if (!player.isShiftKeyDown() && !world.isClientSide) {
      INamedContainerProvider blockEntity = state.getMenuProvider(world,pos);
      NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(VALIDATED, FACING);
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

  @Nullable
  @Override
  public INamedContainerProvider getMenuProvider(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos) {
    return (INamedContainerProvider)worldIn.getBlockEntity(pos);
  }

  @Override
  public void setPlacedBy(World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
    TileEntity tile = worldIn.getBlockEntity(pos);
    if(tile instanceof ApiaryTileEntity) {
      ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tile;
      apiaryTileEntity.setTier(tier);
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
    if(Screen.hasShiftDown())
    {
      tooltip.addAll(new TooltipBuilder()
              .addTranslatableTip("block.resourcefulbees.beehive.tooltip.max_bees")
              .appendText(" " + Config.APIARY_MAX_BEES.get())
              .appendText(" " + I18n.get("block.resourcefulbees.beehive.tooltip.unique_bees"), TextFormatting.BOLD)
              .appendText( TextFormatting.GOLD + " Bees", TextFormatting.RESET)
              .applyStyle(TextFormatting.GOLD)
              .build());
      if (tier != 1) {
        int timeReduction = (int)((0.1 + (tier * .05)) * 100);
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.get("block.resourcefulbees.beehive.tooltip.hive_time"))
                .appendText(" -" + timeReduction + "%")
                .applyStyle(TextFormatting.GOLD)
                .build());
      }
      ApiaryOutputType outputTypeEnum;
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

      String outputType = outputTypeEnum.equals(ApiaryOutputType.COMB) ? I18n.get("honeycomb.resourcefulbees") : I18n.get("honeycomb_block.resourcefulbees");

      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.output_type"))
              .appendText(" " + outputType)
              .applyStyle(TextFormatting.GOLD)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.output_quantity"))
              .appendText(" " + outputQuantity)
              .applyStyle(TextFormatting.GOLD)
              .build());
    }
    else if (Screen.hasControlDown()){
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.structure_size"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.requisites"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.drops"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.tags"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.offset"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.lock"), TextFormatting.AQUA)
              .addTip(I18n.get("block.resourcefulbees.apiary.tooltip.lock_2"), TextFormatting.AQUA)
              .build());
    }
    else
    {
      tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.get("resourcefulbees.shift_info")));
      tooltip.add(new StringTextComponent(TextFormatting.AQUA + I18n.get("resourcefulbees.ctrl_info")));
    }

    super.appendHoverText(stack, worldIn, tooltip, flagIn);
  }
}
