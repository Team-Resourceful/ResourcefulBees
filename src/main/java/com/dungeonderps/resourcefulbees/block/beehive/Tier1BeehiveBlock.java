package com.dungeonderps.resourcefulbees.block.beehive;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier1BeehiveBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Tier1BeehiveBlock extends BeehiveBlock {

  public Tier1BeehiveBlock(Block.Properties properties) {
    super(properties);
  }

  public static void dropResourceHoneycomb(Tier1BeehiveBlock block, World world, BlockPos pos) {
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
	    if (blockEntity instanceof Tier1BeehiveBlockEntity) {
	      Tier1BeehiveBlockEntity hive = (Tier1BeehiveBlockEntity)blockEntity;
	      hive.isSmoked = true;
	    }
  }

  public boolean isHiveSmoked(BlockPos pos, World world) {
	    TileEntity blockEntity = world.getTileEntity(pos);
	    if (blockEntity instanceof Tier1BeehiveBlockEntity) {
	      Tier1BeehiveBlockEntity hive = (Tier1BeehiveBlockEntity)blockEntity;
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
    	if (isHiveSmoked(pos,world) || CampfireBlock.isLitCampfireInRange(world, pos, 5)) {
            this.takeHoney(world, state, pos);
            if (player instanceof ServerPlayerEntity) {
              CriteriaTriggers.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity) player, pos, itemstack1);
            }
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
          beeEntity.setBeeAttacker(playerEntityList.get(world.rand.nextInt(size)));
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

  public void dropResourceHoneycomb(World world, BlockPos pos) {
    TileEntity blockEntity = world.getTileEntity(pos);
    if (blockEntity instanceof Tier1BeehiveBlockEntity) {
      Tier1BeehiveBlockEntity hive = (Tier1BeehiveBlockEntity)blockEntity;
      while (hive.hasCombs()) {
        ItemStack comb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        String honeycomb = hive.getResourceHoneycomb();
        comb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, BeeInfo.getInfo(honeycomb).getHoneycombColor());
        comb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, BeeInfo.getInfo(honeycomb).getName());
        spawnAsEntity(world, pos, comb);
      }
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new Tier1BeehiveBlockEntity();
  }
}
