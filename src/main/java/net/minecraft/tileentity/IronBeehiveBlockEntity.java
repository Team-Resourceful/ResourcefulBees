
package net.minecraft.tileentity;


import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.ResourcefulBees.ObjectHolders;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class IronBeehiveBlockEntity extends BeehiveTileEntity {

  public List<Item> honeycombs = new ArrayList<>();
  public List<String> beeTypes = new ArrayList<>();

  @Nonnull
  @Override
  public TileEntityType<?> getType() {
    return ObjectHolders.BlockEntities.IRON_BEEHIVE;
  }

  @Override
  public boolean releaseBee(BlockState state, CompoundNBT nbt, @Nullable List<Entity> entities, BeehiveTileEntity.State beehiveState) {
    BlockPos blockpos = this.getPos();
    if (shouldStayInHive(state,beehiveState)) {
      return false;
    } else {
      nbt.remove("Passengers");
      nbt.remove("Leash");
      nbt.removeUniqueId("UUID");
      Direction direction = state.get(BeehiveBlock.FACING);
      BlockPos blockpos1 = blockpos.offset(direction);
      if (!this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty()) {
        return false;
      } else {
        Entity entity = EntityType.func_220335_a(nbt, this.world, entity1 -> entity1);
        if (entity != null) {
          float f = entity.getWidth();
          double d0 = 0.55D + f / 2.0F;
          double d1 = blockpos.getX() + 0.5D + d0 * direction.getXOffset();
          double d2 = blockpos.getY() + 0.5D -  (entity.getHeight() / 2.0F);
          double d3 = blockpos.getZ() + 0.5D + d0 * direction.getZOffset();
          entity.setLocationAndAngles(d1, d2, d3, entity.rotationYaw, entity.rotationPitch);
          if (entity instanceof CustomBeeEntity) {
            CustomBeeEntity beeentity = (CustomBeeEntity) entity;
            if (this.hasFlowerPos() && !beeentity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
              beeentity.setFlowerPos(this.flowerPos);
            }

            if (beehiveState == State.HONEY_DELIVERED) {
              beeentity.onHoneyDelivered();
              int i = getHoneyLevel(state);
              if (i < 5) {
                int j = this.world.rand.nextInt(100) == 0 ? 2 : 1;
                if (i + j > 5) {
                  --j;
                }
                this.honeycombs.add(ObjectHolders.Items.RESOURCEFUL_HONEYCOMB);
                this.beeTypes.add(beeentity.getBeeType());
                this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, i + j));
              }
            }
            beeentity.resetTicksWithoutNectar();
            if (entities != null) {
              entities.add(beeentity);
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
    if (this.bees.size() < 3) {
      bee.removePassengers();
      CompoundNBT nbt = new CompoundNBT();
      bee.writeUnlessPassenger(nbt);
      this.bees.add(new BeehiveTileEntity.Bee(nbt, ticksInHive, hasNectar ? 2400 : 600));
      if (this.world != null) {
        if (bee instanceof CustomBeeEntity) {
          CustomBeeEntity bee1 = (CustomBeeEntity)bee;
          if (bee1.hasFlower() && (!this.hasFlowerPos() || this.world.rand.nextBoolean())) {
            this.flowerPos = bee1.getFlowerPos();
          }
        }
        BlockPos lvt_5_2_ = this.getPos();
        this.world.playSound(null, (double)lvt_5_2_.getX(), (double)lvt_5_2_.getY(), (double)lvt_5_2_.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      /*if (bee.getType() == ResourcefulBees.ObjectHolders.Entities.ender_bee){
        this.world.addParticle(ParticleTypes.PORTAL, bee.getPosXRandom(0.5D),
                bee.getPosYRandom() - 0.25D, bee.getPosZRandom(0.5D),
                (world.rand.nextDouble() - 0.5D) * 2.0D, -world.rand.nextDouble(),
                (world.rand.nextDouble() - 0.5D) * 2.0D);
      }*/
      bee.remove();
    }
  }

  public boolean shouldStayInHive(BlockState state, State beehiveState){
    return (this.world.isNightTime() || this.world.isRaining()) && beehiveState != BeehiveTileEntity.State.EMERGENCY;
  }

  @Override
  public boolean isFullOfBees() {
    return bees.size() > 3;
  }

  public Item getResourceHoneyComb(){
    Item honeycomb = honeycombs.get(honeycombs.size()-1);
    honeycombs.remove(honeycomb);
    beeTypes.remove(beeTypes.size()-1);
    return honeycomb;
  }

  public boolean hasCombs(){
    return honeycombs.size() > 0;
  }

  public boolean isAllowedBee(CustomBeeEntity bee){
    Block hive = getBlockState().getBlock();
    return hive == ObjectHolders.Blocks.IRON_BEEHIVE; //|| bee.getAllowedHive() == hive;
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);
    if (nbt.contains("Honeycombs")){
      CompoundNBT combs = (CompoundNBT) nbt.get("Honeycombs");
      CompoundNBT beeData = (CompoundNBT) nbt.get("BeeTypes");
      int i = 0;
      while (combs.contains(String.valueOf(i))){
        honeycombs.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(combs.getString(String.valueOf(i)))));
        beeTypes.add(beeData.getString(String.valueOf(i)));
        i++;
      }
      ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("Honeycomb")));
    }
  }


  //TODO Why the fuck does this cause ticking entity crash?  look above you ^ honeycomb?
  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    LOGGER.info("Honeycombs are empty!! = " + !honeycombs.isEmpty());
    if (!honeycombs.isEmpty()){
      CompoundNBT combs = new CompoundNBT();
      CompoundNBT beeData = new CompoundNBT();
      for (int i = 0; i < honeycombs.size();i++){
        combs.putString(String.valueOf(i),honeycombs.get(i).getRegistryName().toString());
        beeData.putString(String.valueOf(i), beeTypes.get(i));
      }
      nbt.put("Honeycombs",combs);
      nbt.put("BeeTypes", beeData);
    }

    return nbt;
  }

  public static class Bee2 extends Bee {
    public Bee2(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_) {
      super(p_i225767_1_, p_i225767_2_, p_i225767_3_);
    }
  }
}
