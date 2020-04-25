package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.entity.passive.CustomBeeEntity.BEE_INFO;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class IronBeehiveBlock extends BeehiveBlock {

  public IronBeehiveBlock(Block.Properties properties) {
    super(properties);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
    return null;
  }
  
  public void smokeHive(BlockPos pos, World world) {
	    TileEntity blockEntity = world.getTileEntity(pos);
	    if (blockEntity instanceof IronBeehiveBlockEntity) {
	      IronBeehiveBlockEntity hive = (IronBeehiveBlockEntity)blockEntity;
	      hive.smoked = true;
	    }
  }
  
  public boolean isHiveSmoked(BlockPos pos, World world) {
	    TileEntity blockEntity = world.getTileEntity(pos);
	    if (blockEntity instanceof IronBeehiveBlockEntity) {
	      IronBeehiveBlockEntity hive = (IronBeehiveBlockEntity)blockEntity;
	      return hive.smoked;
	    }
	    else
	    return false;
  }

  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    ItemStack itemstack = player.getHeldItem(handIn);
    ItemStack itemstack1 = itemstack.copy();
    int honeyLevel = state.get(HONEY_LEVEL);
    boolean angerBees = false;
   	if (itemstack.getItem() == RegistryHandler.SMOKER.get()) {
   		smokeHive(pos, world);
   		itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));
    }
   	else if (honeyLevel >= 5) {
      if (itemstack.getItem() == Items.SHEARS) {
        world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        dropResourceHoneycomb(world, pos);
        itemstack.damageItem(1, player, player1 -> player1.sendBreakAnimation(handIn));
        angerBees = true;
      } else if (itemstack.getItem() == Items.GLASS_BOTTLE) {
        itemstack.shrink(1);
        world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        if (itemstack.isEmpty()) {
          player.setHeldItem(handIn, new ItemStack(Items.HONEY_BOTTLE));
        } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE))) {
          player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
        }
        angerBees = true;
      }
    }

    if (angerBees) {
    	if (isHiveSmoked(pos,world) || CampfireBlock.isLitCampfireInRange(world, pos, 5)) {
    		LOGGER.info("Smoked: "+ isHiveSmoked(pos,world) + ",Campfire: " + CampfireBlock.isLitCampfireInRange(world, pos, 5));
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
  //needed for Dispenser use, etc.
  public static void dropResourceHoneyComb(IronBeehiveBlock block, World world, BlockPos pos) {
    block.dropResourceHoneycomb(world, pos);
  }

  public void dropResourceHoneycomb(World world, BlockPos pos) {
    TileEntity blockEntity = world.getTileEntity(pos);
    if (blockEntity instanceof IronBeehiveBlockEntity) {
      IronBeehiveBlockEntity hive = (IronBeehiveBlockEntity)blockEntity;
      while (hive.hasCombs()) {
        ItemStack comb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        String honeycomb = hive.getResourceHoneyComb();
        comb.getOrCreateChildTag("ResourcefulBees").putString("Color", BEE_INFO.get(honeycomb).getColor());
        comb.getOrCreateChildTag("ResourcefulBees").putString("BeeType", BEE_INFO.get(honeycomb).getName());
        spawnAsEntity(world, pos, comb);
      }
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new IronBeehiveBlockEntity();
  }
}
