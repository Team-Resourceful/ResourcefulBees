
package net.minecraft.tileentity;


import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;

public class IronBeehiveBlockEntity extends BeehiveTileEntity {

  protected final int TIER = 1;
  protected final float TIER_MODIFIER = 1;

  public Stack<String> honeycombs = new Stack<>();
  public boolean isSmoked = false;
  public int ticksSmoked = 0;

  @Nonnull
  @Override
  public TileEntityType<?> getType() {
    return RegistryHandler.IRON_BEEHIVE_ENTITY.get();
  }

  public int getTier() {
    return TIER;
  }

  public float getTierModifier() {
    return TIER_MODIFIER;
  }

  @Override
  public boolean releaseBee(BlockState state, CompoundNBT nbt, @Nullable List<Entity> entities, BeehiveTileEntity.State beehiveState) {
    BlockPos blockpos = this.getPos();
    if (shouldStayInHive(beehiveState)) {
      return false;
    } else {
      nbt.remove("Passengers");
      nbt.remove("Leash");
      nbt.removeUniqueId("UUID");
      Direction direction = state.get(BeehiveBlock.FACING);
      BlockPos blockpos1 = blockpos.offset(direction);
      if (world != null && !this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty()) {
        return false;
      } else {
        Entity entity = EntityType.loadEntityAndExecute(nbt, this.world, entity1 -> entity1);
        if (entity != null) {
          float f = entity.getWidth();
          double d0 = 0.55D + f / 2.0F;
          double d1 = blockpos.getX() + 0.5D + d0 * direction.getXOffset();
          double d2 = blockpos.getY() + 0.5D -  (entity.getHeight() / 2.0F);
          double d3 = blockpos.getZ() + 0.5D + d0 * direction.getZOffset();
          entity.setLocationAndAngles(d1, d2, d3, entity.rotationYaw, entity.rotationPitch);
          if (entity instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) entity;
            if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
              if (this.flowerPos != null)
                beeEntity.setFlowerPos(this.flowerPos);
            }

            if (beehiveState == State.HONEY_DELIVERED) {
              beeEntity.onHoneyDelivered();
              int i = getHoneyLevel(state);
              if (i < 5) {
                this.honeycombs.push(beeEntity.getBeeType());
                float combsInHive = this.honeycombs.size();
                float maxCombs = Math.round(Config.HIVE_MAX_COMBS.get() * getTierModifier());
                float percentValue = (combsInHive / maxCombs) * 100;
                int newState = (int)(percentValue  - (percentValue % 20))  / 20;
                this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, newState));
              }
            }
            beeEntity.resetTicksWithoutNectar();
            if (entities != null) {
              entities.add(beeEntity);
            }
          }
          BlockPos hivePos = this.getPos();
          this.world.playSound(null, hivePos.getX(), hivePos.getY(),  hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
          return this.world.addEntity(entity);
        } else {
          return false;
        }
      }
    }
  }

  public void tryEnterHive(Entity bee, boolean hasNectar, int ticksInHive) {
    if (this.bees.size() < Math.round(Config.HIVE_MAX_BEES.get() * getTierModifier())) {
      bee.removePassengers();
      CompoundNBT nbt = new CompoundNBT();
      bee.writeUnlessPassenger(nbt);
      if (this.world != null) {
        if (bee instanceof CustomBeeEntity) {
          CustomBeeEntity bee1 = (CustomBeeEntity)bee;
          this.bees.add(new BeehiveTileEntity.Bee(nbt, ticksInHive, hasNectar ? BeeInfo.getInfo(bee1.getBeeType()).getMaxTimeInHive() : BeeConst.MIN_HIVE_TIME));
          if (bee1.hasFlower() && (!this.hasFlowerPos() || this.world.rand.nextBoolean())) {
            this.flowerPos = bee1.getFlowerPos();
          }
        }
        BlockPos pos = this.getPos();
        this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      bee.remove();
    }
  }

  @Override
  public boolean isSmoked() {
	if (world != null)
    return isSmoked || CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
	return false;
  }
  
  @Override
  public void tick() {
    if (world != null && !world.isRemote) {
      if (isSmoked && ticksSmoked < BeeConst.SMOKE_TIME)
        ticksSmoked++;
      if (ticksSmoked == BeeConst.SMOKE_TIME) {
        isSmoked = false;
        ticksSmoked = 0;
      }
    }
    super.tick();
  }
  
  public boolean shouldStayInHive(State beehiveState){
    if (world != null)
    return (this.world.isNightTime() || this.world.isRaining()) && beehiveState != BeehiveTileEntity.State.EMERGENCY;
    return  false;
  }

  @Override
  public boolean isFullOfBees() {
    return bees.size() >= Math.round(Config.HIVE_MAX_BEES.get() * getTierModifier());
  }

  public String getResourceHoneycomb(){
    return honeycombs.pop();
  }

  public boolean hasCombs(){
    return honeycombs.size() > 0;
  }

  public int numberOfCombs() {
      return honeycombs.size();
  }

  public boolean isAllowedBee(){
    Block hive = getBlockState().getBlock();
    return hive == RegistryHandler.IRON_BEEHIVE.get();
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);
    if (nbt.contains(BeeConst.NBT_HONEYCOMBS_TE)){
      CompoundNBT combs = (CompoundNBT) nbt.get(BeeConst.NBT_HONEYCOMBS_TE);
      int i = 0;
      while (combs != null && combs.contains(String.valueOf(i))){
        honeycombs.push(combs.getString(String.valueOf(i)));
        i++;
      }
    }
    if (nbt.contains(BeeConst.NBT_SMOKED_TE)) {
      this.isSmoked = nbt.getBoolean(BeeConst.NBT_SMOKED_TE);
    }
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    if (!honeycombs.isEmpty()){
      CompoundNBT combs = new CompoundNBT();
      for (int i = 0; i < honeycombs.size();i++){
        combs.putString(String.valueOf(i), honeycombs.elementAt(i));
      }
      nbt.put(BeeConst.NBT_HONEYCOMBS_TE,combs);
    }
    nbt.putBoolean(BeeConst.NBT_SMOKED_TE, isSmoked);
    return nbt;
  }

  @Nullable
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return super.getUpdatePacket();
  }
}
