
package com.resourcefulbees.resourcefulbees.tileentity;


import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.mixin.BTEBeeAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.MIN_HIVE_TIME;
import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.SMOKE_TIME;

public class TieredBeehiveTileEntity extends BeehiveTileEntity {

    protected int tier;
    protected float tierModifier;

    private List<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = 0;
    protected int ticksSinceBeesFlagged;

    @Nonnull
    @Override
    public TileEntityType<?> getType() { return ModTileEntityTypes.TIERED_BEEHIVE_TILE_ENTITY.get(); }

    public int getTier() { return tier; }

    public void setTier(int tier){ this.tier = tier; }

    public void setTierModifier(float tierModifier){ this.tierModifier = tierModifier; }

    public float getTierModifier() { return tierModifier; }

    public int getMaxCombs() { return Math.round((float) Config.HIVE_MAX_COMBS.get() * getTierModifier()); }

    public int getMaxBees() { return Math.round((float) Config.HIVE_MAX_BEES.get() * getTierModifier()); }

    public void smokeHive() {
        this.isSmoked = true;
        ticksSmoked = ticksSmoked == -1 ? 0 : ticksSmoked;
    }

    public int getTicksSmoked() { return ticksSmoked; }

    @Override
    public boolean releaseBee(@Nonnull BlockState state, @Nonnull BeehiveTileEntity.Bee tileBee, @Nullable List<Entity> entities, @Nonnull BeehiveTileEntity.State beehiveState) {
        BlockPos blockpos = this.getPos();
        if (shouldStayInHive(beehiveState)) {
            return false;
        } else {
            CompoundNBT nbt = tileBee.entityData;
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Direction direction = state.get(BeehiveBlock.FACING);
            BlockPos blockpos1 = blockpos.offset(direction);
            if (world == null) {
                return false;
            } else {
                if (!this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty() && beehiveState != State.EMERGENCY) {
                    return false;
                }
                Entity entity = EntityType.func_220335_a(nbt, this.world, entity1 -> entity1);
                if (entity != null) {
                    BeeInfoUtils.setEntityLocationAndAngle(blockpos, direction, entity);

                    if (entity instanceof BeeEntity) {
                        BeeEntity vanillaBeeEntity = (BeeEntity) entity;
                        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);

                        if (vanillaBeeEntity.hasFlower() && this.world.rand.nextFloat() > 0.9F) {
                            vanillaBeeEntity.flowerPos = null;
                        }

                        if (beehiveState == State.HONEY_DELIVERED) {
                            vanillaBeeEntity.onHoneyDelivered();
                            int i = getHoneyLevel(state);
                            if (i < 5) {
                                if (entity instanceof ICustomBee && ((ICustomBee)entity).getBeeData().hasHoneycomb()) {
                                    honeycomb = ((ICustomBee)entity).getBeeData().getCombStack();
                                }

                                if (!honeycomb.isEmpty()) this.getHoneycombs().add(0, honeycomb);

                                float combsInHive = this.getHoneycombs().size();
                                float percentValue = (combsInHive / getMaxCombs()) * 100;
                                int newState = (int) MathUtils.clamp((percentValue - (percentValue % 20)) / 20, 0, 5) ;
                                this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, newState));
                            }
                        }

                        BeeInfoUtils.ageBee(((BTEBeeAccessor) tileBee).getTicksInHive(), vanillaBeeEntity);
                        if (entities != null) entities.add(entity);
                    }
                    BlockPos hivePos = this.getPos();
                    this.world.playSound(null, hivePos.getX(), hivePos.getY(),  hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return this.world.addEntity(entity);
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public void tryEnterHive(@Nonnull Entity bee, boolean hasNectar, int ticksInHive) {
        if (this.bees.size() < getMaxBees()) {
            bee.removePassengers();
            CompoundNBT nbt = new CompoundNBT();
            bee.writeUnlessPassenger(nbt);

            if (this.world != null && bee instanceof BeeEntity) {
                int maxTimeInHive = getMaxTimeInHive(bee instanceof ICustomBee ? ((ICustomBee) bee).getBeeData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
                this.bees.add(new BeehiveTileEntity.Bee(nbt, ticksInHive,  hasNectar ? maxTimeInHive : MIN_HIVE_TIME));
                BlockPos pos = this.getPos();
                this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                bee.remove();
            }
        }
    }

    private int getMaxTimeInHive(int timeInput) {
        if (this.tier != 1) {
            if (this.tier == 0) {
                return (int) (timeInput * 1.05);
            } else {
                return (int) (timeInput * (1 - getTier() * .05));
            }
        }
        return timeInput;
    }

    @Override
    public boolean isSmoked() {
        assert this.world != null;
        return isSmoked || CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            if (isSmoked) {
                if (MathUtils.inRangeInclusive(ticksSmoked, 0, SMOKE_TIME)) {
                    ticksSmoked++;
                } else {
                    isSmoked = false;
                    ticksSmoked = -1;
                }
            }

            ticksSinceBeesFlagged++;
            if (ticksSinceBeesFlagged == 80) {
                BeeInfoUtils.flagBeesInRange(pos, world);
                ticksSinceBeesFlagged = 0;
            }
        }
        super.tick();
    }

    public boolean shouldStayInHive(State beehiveState){
        return (world != null && (this.world.isNight() || this.world.isRaining())) && beehiveState != BeehiveTileEntity.State.EMERGENCY;
    }

    @Override
    public boolean isFullOfBees() { return bees.size() >= getMaxBees(); }

    public boolean hasBees() { return !bees.isEmpty(); }

    public ItemStack getResourceHoneycomb(){ return getHoneycombs().remove(0); }

    public boolean hasCombs(){ return numberOfCombs() > 0; }

    public int numberOfCombs() { return getHoneycombs().size(); }

    public boolean isAllowedBee(){
        Block hive = getBlockState().getBlock();
        return hive instanceof TieredBeehiveBlock;
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.fromTag(state, nbt);
        if (nbt.contains(NBTConstants.NBT_HONEYCOMBS_TE)) honeycombs = getHoneycombs(nbt);
        if (nbt.contains(NBTConstants.NBT_SMOKED_TE)) this.isSmoked = nbt.getBoolean(NBTConstants.NBT_SMOKED_TE);
        if (nbt.contains(NBTConstants.NBT_TIER)) setTier(nbt.getInt(NBTConstants.NBT_TIER));
        if (nbt.contains(NBTConstants.NBT_TIER_MODIFIER)) setTierModifier(nbt.getFloat(NBTConstants.NBT_TIER_MODIFIER));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        if (!getHoneycombs().isEmpty()) nbt.put(NBTConstants.NBT_HONEYCOMBS_TE, writeHoneycombs());
        nbt.putBoolean(NBTConstants.NBT_SMOKED_TE, isSmoked);
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
        nbt.putFloat(NBTConstants.NBT_TIER_MODIFIER, getTierModifier());
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() { return super.getUpdatePacket(); }

    public ListNBT writeHoneycombs() {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < numberOfCombs(); i++) {
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.putInt(String.valueOf(i), i);
            getHoneycombs().get(i).write(itemTag);
            nbtTagList.add(itemTag);
        }
        return nbtTagList;
    }

    public List<ItemStack> getHoneycombs(CompoundNBT nbt) {
        List<ItemStack> itemStacks = new LinkedList<>();
        ListNBT tagList = nbt.getList(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_COMPOUND);
        tagList.forEach(compound -> itemStacks.add(0, ItemStack.read((CompoundNBT) compound)));
        return itemStacks;
    }

    public List<ItemStack> getHoneycombs() {
        return honeycombs;
    }
}
