package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.dungeonderps.resourcefulbees.utils.NBTHelper;
import com.dungeonderps.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TieredBeehiveBlock extends BeehiveBlock {

  private final int TIER;
  private final float TIER_MODIFIER;

  public TieredBeehiveBlock(final int tier, final float tierModifier, Properties properties) {
    super(properties);
    TIER = tier;
    TIER_MODIFIER = tierModifier;
  }

  public static void dropResourceHoneycomb(TieredBeehiveBlock block, World world, BlockPos pos) {
    block.dropResourceHoneycomb(world, pos);
  }

  @Override
  protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
  }
  
  @Nullable
  @Override
  public TileEntity createNewTileEntity(@Nonnull IBlockReader reader) {
    return null;
  }
  
  public void smokeHive(BlockPos pos, World world) {
	    TileEntity blockEntity = world.getTileEntity(pos);
	    if (blockEntity instanceof TieredBeehiveTileEntity) {
	      TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity)blockEntity;
	      hive.isSmoked = true;
	    }
  }

  public boolean isHiveSmoked(BlockPos pos, World world) {
	    TileEntity blockEntity = world.getTileEntity(pos);
	    if (blockEntity instanceof TieredBeehiveTileEntity) {
	      TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity)blockEntity;
	      return hive.isSmoked;
	    }
	    else
	    return false;
  }

    @Nonnull
  public ActionResultType onBlockActivated(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
    ItemStack itemstack = player.getHeldItem(handIn);
    ItemStack itemstack1 = itemstack.copy();
    int honeyLevel = state.get(HONEY_LEVEL);
    boolean angerBees = false;
   	if (itemstack.getItem() == RegistryHandler.SMOKER.get()) {
   		smokeHive(pos, world);
   		itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));
    }
   	else if (honeyLevel >= 5) {
      if (itemstack.getItem().isIn(ItemTags.getCollection().getOrCreate(new ResourceLocation("forge:shears")))) {
        world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(world, pos);
        itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));
        angerBees = true;
      }
    }

    if (angerBees) {
    	if (isHiveSmoked(pos,world) || CampfireBlock.isSmokingBlockAt(world, pos)) {
            this.takeHoney(world, state, pos);
    	}
    	else {
            if (this.hasBees(world, pos)) {
          	  this.angerNearbyBees(world, pos);
            }

              this.takeHoney(world, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
    	}
      return ActionResultType.SUCCESS;
    } else {
      return ActionResultType.PASS;
    }
  }

  private void angerNearbyBees(World world, BlockPos pos) {
    List<BeeEntity> beeEntityList = world.getEntitiesWithinAABB(BeeEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
    if (!beeEntityList.isEmpty()) {
      List<PlayerEntity> playerEntityList = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
      int size = playerEntityList.size();

      for (BeeEntity beeEntity : beeEntityList) {
        if (beeEntity.getAttackTarget() == null) {
          beeEntity.setAttackTarget(playerEntityList.get(world.rand.nextInt(size)));
        }
      }
    }
  }

  private boolean hasBees(World world, BlockPos pos) {
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof BeehiveTileEntity) {
      BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity) tileEntity;
      return !beehiveTileEntity.hasNoBees();
    } else {
      return false;
    }
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    if(Screen.hasShiftDown())
    {
      tooltip.addAll(new TooltipBuilder()
              .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_bees"))
              .appendText(" " + Math.round(Config.HIVE_MAX_BEES.get() * TIER_MODIFIER))
              .applyStyle(TextFormatting.GOLD)
              .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.max_combs"))
              .appendText(" " + Math.round(Config.HIVE_MAX_COMBS.get() * TIER_MODIFIER))
              .applyStyle(TextFormatting.GOLD)
              .build());
      if (TIER != 1) {
        int time_reduction = TIER > 1 ? (int) ((TIER * .05) * 100) : (int) (.05 * 100);
        String sign = TIER > 1 ? "-" : "+";
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.format("block.resourcefulbees.beehive.tooltip.hive_time"))
                .appendText(" " + sign + time_reduction + "%")
                .applyStyle(TextFormatting.GOLD)
                .build());
      }
    }
    else
    {
      tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("resourcefulbees.shift_info")));
    }

    super.addInformation(stack, worldIn, tooltip, flagIn);
  }

  public void dropResourceHoneycomb(World world, BlockPos pos) {
    TileEntity blockEntity = world.getTileEntity(pos);
    if (blockEntity instanceof TieredBeehiveTileEntity) {
      TieredBeehiveTileEntity hive = (TieredBeehiveTileEntity)blockEntity;
      while (hive.hasCombs()) {
        ItemStack comb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        String honeycomb = hive.getResourceHoneycomb();
        comb.setTag(NBTHelper.createHoneycombItemTag(BeeInfo.getInfo(honeycomb).getName(), BeeInfo.getInfo(honeycomb).getHoneycombColor()));
        spawnAsEntity(world, pos, comb);
      }
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TieredBeehiveTileEntity();
  }

  @Override
  public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
    TileEntity tile = worldIn.getTileEntity(pos);
    if(tile instanceof TieredBeehiveTileEntity) {
      TieredBeehiveTileEntity tieredBeehiveTileEntity = (TieredBeehiveTileEntity) tile;
      tieredBeehiveTileEntity.setTier(TIER);
      tieredBeehiveTileEntity.setTierModifier(TIER_MODIFIER);
    }
  }
}
