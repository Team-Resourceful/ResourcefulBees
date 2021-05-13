
package com.resourcefulbees.resourcefulbees.tileentity;


import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.mixin.BTEBeeAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.MIN_HIVE_TIME;
import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.SMOKE_TIME;

public class TieredBeehiveTileEntity extends BeehiveBlockEntity {

    protected int tier;
    protected float tierModifier;

    private List<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = -1;
    protected int ticksSinceBeesFlagged;

    public TieredBeehiveTileEntity() {
        super();
    }
    public TieredBeehiveTileEntity(int tier, float tierModifier) {
        super();
        this.tier = tier;
        this.tierModifier = tierModifier;
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() { return ModBlockEntityTypes.TIERED_BEEHIVE_TILE_ENTITY.get(); }

    public int getTier() { return tier; }

    public void setTier(int tier){ this.tier = tier; }

    public void setTierModifier(float tierModifier){ this.tierModifier = tierModifier; }

    public float getTierModifier() { return tierModifier; }

    public int getMaxCombs() { return Math.round((float) Config.HIVE_MAX_COMBS.get() * getTierModifier()); }

    public int getMaxBees() { return Math.round((float) Config.HIVE_MAX_BEES.get() * getTierModifier()); }

    public void recalculateHoneyLevel() {
        float combsInHive = this.getHoneycombs().size();
        float percentValue = (combsInHive / getMaxCombs()) * 100;
        int newState = (int) MathUtils.clamp((percentValue - (percentValue % 20)) / 20, 0, 5) ;
        assert this.level != null;
        this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BeehiveBlock.HONEY_LEVEL, newState));
    }

    public void smokeHive() {
        this.isSmoked = true;
        ticksSmoked = ticksSmoked == -1 ? 0 : ticksSmoked;
    }

    public int getTicksSmoked() { return ticksSmoked; }

    @Override
    public boolean releaseOccupant(@NotNull BlockState state, @NotNull BeehiveBlockEntity.BeeData tileBee, @Nullable List<Entity> entities, @NotNull BeehiveBlockEntity.BeeReleaseStatus beehiveState) {
        BlockPos blockpos = this.getBlockPos();
        if (shouldStayInHive(beehiveState)) {
            return false;
        } else {
            CompoundTag nbt = tileBee.entityData;
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Direction direction = state.getValue(BeehiveBlock.FACING);
            BlockPos blockpos1 = blockpos.relative(direction);
            if (level == null) {
                return false;
            } else {
                if (!this.level.getBlockState(blockpos1).getCollisionShape(this.level, blockpos1).isEmpty() && beehiveState != BeeReleaseStatus.EMERGENCY) {
                    return false;
                }
                Entity entity = EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1);
                if (entity != null) {
                    BeeInfoUtils.setEntityLocationAndAngle(blockpos, direction, entity);

                    if (entity instanceof Bee) {
                        Bee vanillaBeeEntity = (Bee) entity;
                        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);

                        if (beehiveState == BeeReleaseStatus.HONEY_DELIVERED) {
                            vanillaBeeEntity.dropOffNectar();
                            int i = getHoneyLevel(state);
                            if (i < 5) {
                                if (entity instanceof ICustomBee && !((ICustomBee)entity).getHoneycombData().getHoneycombType().equals(HoneycombTypes.NONE)) {
                                    honeycomb = ((ICustomBee)entity).getHoneycombData().getHoneycomb().getDefaultInstance();
                                }

                                if (!honeycomb.isEmpty()) this.honeycombs.add(0, honeycomb);

                                recalculateHoneyLevel();
                            }
                        }

                        BeeInfoUtils.ageBee(((BTEBeeAccessor) tileBee).getTicksInHive(), vanillaBeeEntity);
                        if (entities != null) entities.add(entity);
                    }
                    BlockPos hivePos = this.getBlockPos();
                    this.level.playSound(null, hivePos.getX(), hivePos.getY(),  hivePos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return this.level.addFreshEntity(entity);
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public void addOccupantWithPresetTicks(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        if (this.stored.size() < getMaxBees()) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);

            if (this.level != null && bee instanceof Bee) {
                int maxTimeInHive = getMaxTimeInHive(bee instanceof ICustomBee ? ((ICustomBee) bee).getCoreData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
                this.stored.add(new BeeData(nbt, ticksInHive,  hasNectar ? maxTimeInHive : MIN_HIVE_TIME));
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
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
    public boolean isSedated() {
        assert this.level != null;
        return isSmoked || CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
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
                BeeInfoUtils.flagBeesInRange(worldPosition, level);
                ticksSinceBeesFlagged = 0;
            }
        }
        super.tick();
    }

    public boolean shouldStayInHive(BeeReleaseStatus beehiveState){
        return (level != null && (this.level.isNight() || this.level.isRaining())) && beehiveState != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY;
    }

    @Override
    public boolean isFull() { return stored.size() >= getMaxBees(); }

    public boolean hasBees() { return !stored.isEmpty(); }

    public ItemStack getResourceHoneycomb(){ return honeycombs.remove(0); }

    public boolean hasCombs(){ return numberOfCombs() > 0; }

    public int numberOfCombs() { return getHoneycombs().size(); }

    public boolean isAllowedBee(){
        Block hive = getBlockState().getBlock();
        return hive instanceof TieredBeehiveBlock;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains(NBTConstants.NBT_HONEYCOMBS_TE)) honeycombs = getHoneycombs(nbt);
        if (nbt.contains(NBTConstants.NBT_SMOKED_TE)) this.isSmoked = nbt.getBoolean(NBTConstants.NBT_SMOKED_TE);
        if (nbt.contains(NBTConstants.NBT_TIER)) setTier(nbt.getInt(NBTConstants.NBT_TIER));
        if (nbt.contains(NBTConstants.NBT_TIER_MODIFIER)) setTierModifier(nbt.getFloat(NBTConstants.NBT_TIER_MODIFIER));
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        if (!getHoneycombs().isEmpty()) nbt.put(NBTConstants.NBT_HONEYCOMBS_TE, writeHoneycombs());
        nbt.putBoolean(NBTConstants.NBT_SMOKED_TE, isSmoked);
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
        nbt.putFloat(NBTConstants.NBT_TIER_MODIFIER, getTierModifier());
        return nbt;
    }

    public ListTag writeHoneycombs() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < numberOfCombs(); i++) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt(String.valueOf(i), i);
            getHoneycombs().get(i).save(itemTag);
            nbtTagList.add(itemTag);
        }
        return nbtTagList;
    }

    public List<ItemStack> getHoneycombs(CompoundTag nbt) {
        List<ItemStack> itemStacks = new LinkedList<>();
        ListTag tagList = nbt.getList(NBTConstants.NBT_HONEYCOMBS_TE, Constants.NBT.TAG_COMPOUND);
        tagList.forEach(compound -> itemStacks.add(0, ItemStack.of((CompoundTag) compound)));
        return itemStacks;
    }

    public List<ItemStack> getHoneycombs() {
        return Collections.unmodifiableList(honeycombs);
    }
}
