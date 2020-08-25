package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.lib.ApiaryOutput;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import com.dungeonderps.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
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
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ApiaryBlock extends Block {

  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static final BooleanProperty VALIDATED = BooleanProperty.create("validated");

  private final int TIER;

  public ApiaryBlock(final int tier, float hardness, float resistance) {
    super(Properties.create(Material.IRON).hardnessAndResistance(hardness, resistance).sound(SoundType.METAL));
    TIER = tier;
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

  @Override
  public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
    TileEntity tile = worldIn.getTileEntity(pos);
    if(tile instanceof ApiaryTileEntity) {
      ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tile;
      apiaryTileEntity.setTier(TIER);
    }
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    if(Screen.hasShiftDown())
    {
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_bees"))
              .appendText(" " + Config.APIARY_MAX_BEES.get())
              .appendText(" " + I18n.format("block.resourcefulbees.beehive.tooltip.unique_bees"), TextFormatting.BOLD)
              .appendText( TextFormatting.GOLD + " Bees", TextFormatting.RESET)
              .applyStyle(TextFormatting.GOLD)
              .build());
      if (TIER != 1) {
        int time_reduction = (int)((0.1 + (TIER * .05)) * 100);
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.hive_time"))
                .appendText(" -" + time_reduction + "%")
                .applyStyle(TextFormatting.GOLD)
                .build());
      }
      ApiaryOutput outputTypeEnum;
      int outputQuantity;

      switch (TIER) {
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

      String outputType = outputTypeEnum.equals(ApiaryOutput.COMB) ? I18n.format("honeycomb.resourcefulbees") : I18n.format("honeycomb_block.resourcefulbees");

      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.output_type"))
              .appendText(" " + outputType)
              .applyStyle(TextFormatting.GOLD)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.output_quantity"))
              .appendText(" " + outputQuantity)
              .applyStyle(TextFormatting.GOLD)
              .build());
    }
    else if (Screen.hasControlDown()){
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.structure_size"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.requisites"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.drops"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.tags"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.offset"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.lock"), TextFormatting.AQUA)
              .addTip(I18n.format("block.resourcefulbees.apiary.tooltip.lock_2"), TextFormatting.AQUA)
              .build());
    }
    else
    {
      tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("resourcefulbees.shift_info")));
      tooltip.add(new StringTextComponent(TextFormatting.AQUA + I18n.format("resourcefulbees.ctrl_info")));
    }

    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}
