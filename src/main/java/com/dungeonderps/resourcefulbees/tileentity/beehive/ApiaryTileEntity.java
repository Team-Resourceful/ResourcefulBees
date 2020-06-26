
package com.dungeonderps.resourcefulbees.tileentity.beehive;


import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.beehive.ApiaryBlock;
import com.dungeonderps.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.container.ValidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryStorageTileEntity;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ApiaryTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

  public static long start;
  public static long end;

  public final HashMap<String, Bee> bees = new HashMap<>();
  public final List<BlockPos> STRUCTURE_BLOCKS = new ArrayList<>();

  private final Tag<Block> validApiaryTag;

  protected final int TIER = 5;
  protected final float TIER_MODIFIER = 5;

  public Stack<String> honeycombs = new Stack<>();
  public boolean isSmoked = false;
  public int ticksSmoked = 0;
  public ApiaryStorageTileEntity apiaryStorage;
  public boolean isValidApiary;

  private int ticksSinceValidation = 290;


  public ApiaryTileEntity() {
    super(RegistryHandler.APIARY_TILE_ENTITY.get());
    validApiaryTag = BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary");
  }

  public static class Bee {
    private final CompoundNBT entityData;
    private int ticksInHive;
    private final int minOccupationTicks;
    private final BlockPos savedFlowerPos;

    public Bee(CompoundNBT nbt, int ticksinhive, int minoccupationticks) {
      nbt.removeUniqueId("UUID");
      this.entityData = nbt;
      this.ticksInHive = ticksinhive;
      this.minOccupationTicks = minoccupationticks;
      this.savedFlowerPos = null;
    }

    public Bee(CompoundNBT nbt, int ticksinhive, int minoccupationticks, BlockPos flowerPos) {
      nbt.removeUniqueId("UUID");
      this.entityData = nbt;
      this.ticksInHive = ticksinhive;
      this.minOccupationTicks = minoccupationticks;
      this.savedFlowerPos = flowerPos;
    }
  }

  public int getTier() {
    return TIER;
  }

  public float getTierModifier() {
    return TIER_MODIFIER;
  }

  public int getMaxCombs() {
    return 0;
  }

  public int getMaxBees() {
    return 9;
  }

  public int getBeeCount() {
    return this.bees.size();
  }

  public void angerBees(@Nullable PlayerEntity playerEntity, @Nonnull BlockState blockState, @Nonnull State state) {
    List<Entity> list = this.tryReleaseBee(blockState, state);
    if (playerEntity != null) {
      for(Entity entity : list) {
        if (entity instanceof BeeEntity) {
          BeeEntity beeentity = (BeeEntity)entity;
          if (playerEntity.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 16.0D) {
            if (!this.isSmoked()) {
              beeentity.setBeeAttacker(playerEntity);
            } else {
              beeentity.setStayOutOfHiveCountdown(400);
            }
          }
        }
      }
    }
  }

  private List<Entity> tryReleaseBee(BlockState blockState, State state) {
    List<Entity> list = Lists.newArrayList();
    this.bees.entrySet().removeIf((beeEntry) -> this.releaseBee(blockState, beeEntry.getValue().entityData, list, state, beeEntry.getValue().savedFlowerPos));
    return list;
  }

  public boolean releaseBee(@Nonnull BlockState state, @Nonnull CompoundNBT nbt, @Nullable List<Entity> entities, @Nonnull State beehiveState) {
    return releaseBee(state, nbt, entities, beehiveState, null);
  }

  public boolean releaseBee(@Nonnull BlockState state, @Nonnull CompoundNBT nbt, @Nullable List<Entity> entities, @Nonnull State beehiveState, @Nullable BlockPos flowerPos) {
    return releaseBee(state, nbt, entities, beehiveState, flowerPos, false);
  }

  public boolean releaseBee(@Nonnull BlockState state, @Nonnull CompoundNBT nbt, @Nullable List<Entity> entities, @Nonnull State beehiveState, @Nullable BlockPos flowerPos, boolean exportBee) {
    BlockPos blockpos = this.getPos();
    if (!exportBee && shouldStayInHive(beehiveState)) {
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
            if (flowerPos != null && !beeEntity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
              beeEntity.setFlowerPos(flowerPos);
            }

            if (beehiveState == State.HONEY_DELIVERED) {
              beeEntity.onHoneyDelivered();
              if (!exportBee && !beeEntity.getBeeInfo().getMainOutput().isEmpty()) {
                if (isValidApiary) {
                }
              }
            }

            beeEntity.resetTicksWithoutNectar();
            if (entities != null) {
              entities.add(beeEntity);
            }
          }
          BlockPos hivePos = this.getPos();
          this.world.playSound(null, hivePos.getX(), hivePos.getY(),  hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
          this.world.addEntity(entity);
          if (exportBee) {
            //TODO - add bee to jar
          }
          return true;
        } else {
          return false;
        }
      }
    }
  }

  public void tryEnterHive(Entity bee, boolean hasNectar) {
    this.tryEnterHive(bee, hasNectar, 0);
  }

  public void tryEnterHive(@Nonnull Entity bee, boolean hasNectar, int ticksInHive) {
    if (this.world != null) {
      if (bee instanceof CustomBeeEntity) {
        CustomBeeEntity bee1 = (CustomBeeEntity)bee;
        String type = bee1.getBeeType();

        if (!this.bees.containsKey(type) && this.bees.size() < getMaxBees()) {
          bee.removePassengers();
          CompoundNBT nbt = new CompoundNBT();
          bee.writeUnlessPassenger(nbt);

          int maxTimeInHive = bee1.getBeeInfo().getMaxTimeInHive();
          maxTimeInHive = this.getTier() > 1 ? (int) (maxTimeInHive * (1 - getTier() * .05)) : maxTimeInHive;
          int finalMaxTimeInHive = maxTimeInHive;
          if (bee1.hasFlower())
            this.bees.computeIfAbsent(bee1.getBeeType(), k-> new Bee(nbt, ticksInHive,  hasNectar ? finalMaxTimeInHive : BeeConst.MIN_HIVE_TIME, bee1.getFlowerPos()));
          else
            this.bees.computeIfAbsent(bee1.getBeeType(), k-> new Bee(nbt, ticksInHive,  hasNectar ? finalMaxTimeInHive : BeeConst.MIN_HIVE_TIME));

          BlockPos pos = this.getPos();
          this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);

          bee.remove();
        }
      }
    }
  }

  public boolean isSmoked() {
	if (world != null)
    return isSmoked || CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
	return false;
  }
  
  @Override
  public void tick() {
    if (world != null && !world.isRemote) {
      if (isValidApiary) {
        if (ticksSinceValidation >= 300)
          runStructureValidation(null);
        else
          ticksSinceValidation++;
      }
      if (isSmoked && ticksSmoked < BeeConst.SMOKE_TIME)
        ticksSmoked++;
      if (ticksSmoked == BeeConst.SMOKE_TIME) {
        isSmoked = false;
        ticksSmoked = 0;
      }
    }

    if (world != null && !this.world.isRemote) {
      this.tickBees();
      BlockPos blockpos = this.getPos();
      if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
        double d0 = blockpos.getX() + 0.5D;
        double d1 = blockpos.getY();
        double d2 = blockpos.getZ() + 0.5D;
        this.world.playSound(null, d0, d1, d2, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

    }
  }

  private void tickBees() {
    Iterator<Map.Entry<String, Bee>> iterator = this.bees.entrySet().iterator();
    BlockState blockstate = this.getBlockState();

    while(iterator.hasNext()) {
      Map.Entry<String, Bee> element = iterator.next();
      Bee bee = element.getValue();
      if (bee.ticksInHive > bee.minOccupationTicks) {

        CompoundNBT compoundnbt = bee.entityData;
        State state = compoundnbt.getBoolean("HasNectar") ? State.HONEY_DELIVERED : State.BEE_RELEASED;
        if (this.releaseBee(blockstate, compoundnbt, null, state, bee.savedFlowerPos)) {
          iterator.remove();
        }
      } else {
        bee.ticksInHive++;
      }
    }
  }

  @Nonnull
  public ListNBT getBees() {
    ListNBT listnbt = new ListNBT();

    this.bees.forEach((key, bee) -> {
      bee.entityData.removeUniqueId("UUID");
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("EntityData", bee.entityData);
      compoundnbt.putInt("TicksInHive", bee.ticksInHive);
      compoundnbt.putInt("MinOccupationTicks", bee.minOccupationTicks);
      listnbt.add(compoundnbt);
    });

    return listnbt;
  }

  public boolean shouldStayInHive(State beehiveState){
    if (world != null)
    return (this.world.isNightTime() || this.world.isRaining()) && beehiveState != State.EMERGENCY;
    return  false;
  }

  public boolean isFullOfBees() {
    return bees.size() >= getMaxBees();
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
    return isValidApiary && hive == RegistryHandler.APIARY_BLOCK.get();
  }

  @Override
  public void read(@Nonnull CompoundNBT nbt) {
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
    if (nbt.contains("isValid"))
      this.isValidApiary = nbt.getBoolean("isValid");
  }

  @Nonnull
  @Override
  public CompoundNBT write(@Nonnull CompoundNBT nbt) {
    super.write(nbt);
    if (!honeycombs.isEmpty()){
      CompoundNBT combs = new CompoundNBT();
      for (int i = 0; i < honeycombs.size();i++){
        combs.putString(String.valueOf(i), honeycombs.elementAt(i));
      }
      nbt.put(BeeConst.NBT_HONEYCOMBS_TE,combs);
    }
    nbt.putBoolean(BeeConst.NBT_SMOKED_TE, isSmoked);
    nbt.putBoolean("isValid", isValidApiary);
    return nbt;
  }

  @Nullable
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return super.getUpdatePacket();
  }

  public void runStructureValidation(@Nullable ServerPlayerEntity validatingPlayer){
    if (this.world != null && !this.world.isRemote()) {
      start = System.currentTimeMillis();
      if (!this.isValidApiary || STRUCTURE_BLOCKS.isEmpty())
        buildStructureBlockList();
      this.isValidApiary = validateStructure(this.world, validatingPlayer);
      this.world.setBlockState(this.getPos(), getBlockState().with(ApiaryBlock.VALIDATED, this.isValidApiary));
      if (validatingPlayer != null && this.isValidApiary){
        markDirty();
        NetworkHooks.openGui(validatingPlayer, this, this.getPos());
      }
      this.ticksSinceValidation = 0;
      end = System.currentTimeMillis();
      ResourcefulBees.LOGGER.debug((end-start) + "ms");
    }
  }

  public boolean validateStructure(World worldIn, @Nullable ServerPlayerEntity validatingPlayer){
    boolean isStructureValid = true;
    for (BlockPos pos : STRUCTURE_BLOCKS){
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block.isIn(validApiaryTag)){
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof ApiaryStorageTileEntity) {
          if (apiaryStorage == null || apiaryStorage.getPos() != pos)
            apiaryStorage = (ApiaryStorageTileEntity) tile;
        }
      } else {
        isStructureValid = false;
        if (validatingPlayer != null) {
          validatingPlayer.sendMessage(new StringTextComponent("Block at position (" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + ") is invalid!"));
        }
      }
    }
    if (validatingPlayer != null) {
      validatingPlayer.sendMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.validated." + isStructureValid));
    }

    return isStructureValid;
  }

  private MutableBoundingBox buildStructureBounds(){
    MutableBoundingBox box;
    int posX = this.getPos().getX();
    int posY = this.getPos().getY();
    int posZ = this.getPos().getZ();

    switch (this.getBlockState().get(ApiaryBlock.FACING)){
      case NORTH:
        posX -= 3;
        posY -= 2;
        box = new MutableBoundingBox(posX, posY, posZ ,posX + 6,posY + 5,posZ - 6);
        break;
      case EAST:
        posZ -= 3;
        posY -= 2;
        box = new MutableBoundingBox(posX, posY, posZ ,posX + 6,posY + 5,posZ + 6);
        break;
      case SOUTH:
        posX -= 3;
        posY -= 2;
        box = new MutableBoundingBox(posX, posY, posZ ,posX + 6,posY + 5,posZ + 6);
        break;
      default:
        posZ -= 3;
        posY -= 2;
        box = new MutableBoundingBox(posX, posY, posZ ,posX - 6,posY + 5,posZ + 6);
    }
    return box;
  }

  private void buildStructureBlockList(){
    if (this.world != null) {
      MutableBoundingBox box = buildStructureBounds();
      STRUCTURE_BLOCKS.clear();
      BlockPos.getAllInBox(box).forEach((blockPos -> {
        if (blockPos.getX() == box.minX || blockPos.getX() == box.maxX ||
            blockPos.getY() == box.minY || blockPos.getY() == box.maxY ||
            blockPos.getZ() == box.minZ || blockPos.getZ() == box.maxZ ){
              BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
              STRUCTURE_BLOCKS.add(savedPos);
              TileEntity tile = this.world.getTileEntity(blockPos);
              if (tile instanceof ApiaryStorageTileEntity) {
                apiaryStorage = (ApiaryStorageTileEntity) tile;
              }
        }
      }));
    }
  }



  @Nullable
  @Override
  public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
    if (world != null)
      if (this.isValidApiary)
        return new ValidatedApiaryContainer(i, world, pos, playerInventory);
      else
        return new UnvalidatedApiaryContainer(i, world, pos, playerInventory);
    return null;
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
     return new TranslationTextComponent("gui.resourcefulbees.apiary");
  }

  public enum State {
    HONEY_DELIVERED,
    BEE_RELEASED,
    EMERGENCY
  }
}
