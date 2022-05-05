
package com.teamresourceful.resourcefulbees.common.blockentity;


import com.google.common.collect.Lists;
import com.teamresourceful.resourcefulbees.api.IBeeCompat;
import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveBeeDataAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;
import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.SMOKE_TIME;

public class TieredBeehiveBlockEntity extends BeehiveBlockEntity {

    private final RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> entityType;
    private Queue<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = -1;
    protected int ticksSinceBeesFlagged;

    public TieredBeehiveBlockEntity(RegistryObject<BlockEntityType<TieredBeehiveBlockEntity>> entityType, BlockPos pos, BlockState state) {
        super(pos,state);
        this.entityType = entityType;
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() { return entityType.get(); }


    public static void recalculateHoneyLevel(TieredBeehiveBlockEntity hive) {
        float combsInHive = hive.honeycombs.size();
        float percentValue = (combsInHive / hive.getBlock().getTier().getMaxCombs()) * 100;
        int newState = (int) Mth.clamp((percentValue - (percentValue % 20)) / 20, 0, 5) ;
        if (hive.level != null) hive.level.setBlockAndUpdate(hive.worldPosition, hive.getBlockState().setValue(BeehiveBlock.HONEY_LEVEL, newState));
    }

    public void smokeHive() {
        this.isSmoked = true;
        ticksSmoked = ticksSmoked == -1 ? 0 : ticksSmoked;
    }

    public int getTicksSmoked() { return ticksSmoked; }

    @Override
    public void emptyAllLivingFromHive(@Nullable Player player, @NotNull BlockState state, BeehiveBlockEntity.@NotNull BeeReleaseStatus status) {
        if (player == null) return;
        this.releaseAllBees(state, status)
                .stream()
                .filter(e -> e.position().distanceToSqr(player.position()) <= 16.0D)
                .forEach(entity -> {
                        if (!this.isSedated()) {
                            if (entity instanceof Mob mob) mob.setTarget(player);
                            else if (entity instanceof IBeeCompat compat) compat.setOutOfHiveCooldown(400);
                        }
                });
    }

    private List<Entity> releaseAllBees(BlockState state, BeehiveBlockEntity.BeeReleaseStatus status) {
        List<Entity> list = Lists.newArrayList();
        ((BeehiveEntityAccessor)this).getBees().removeIf(beeData -> releaseBee(this, state, beeData, list, status));
        return list;
    }

    private static boolean releaseBee(TieredBeehiveBlockEntity hive, @NotNull BlockState state, @NotNull BeehiveBlockEntity.BeeData tileBee, @Nullable List<Entity> entities, @NotNull BeeReleaseStatus beehiveState) {
        if (shouldStayInHive(hive.level, beehiveState)) {
            return false;
        } else {
            CompoundTag nbt = ((BeehiveBeeDataAccessor) tileBee).getEntityData();
            BeehiveEntityAccessor.callRemoveIgnoredBeeTags(nbt);
            nbt.put("HivePos", NbtUtils.writeBlockPos(hive.getBlockPos()));
            nbt.putBoolean("NoGravity", true);
            Direction direction = state.getValue(BeehiveBlock.FACING);
            BlockPos relative = hive.worldPosition.relative(direction);

            if (hive.level == null) return false;
            if (!hive.level.getBlockState(relative).getCollisionShape(hive.level, relative).isEmpty() && beehiveState != BeeReleaseStatus.EMERGENCY) {
                return false;
            }
            Entity entity = EntityType.loadEntityRecursive(nbt, hive.level, entity1 -> entity1);
            if (entity != null) {
                BeeInfoUtils.setEntityLocationAndAngle(hive.worldPosition, direction, entity);
                if (entity instanceof IBeeCompat compat && beehiveState == BeeReleaseStatus.HONEY_DELIVERED) {
                    compat.nectarDroppedOff();
                    if (getHoneyLevel(state) < 5) {
                        compat.getOptionalHiveOutput(hive.getBlock().getTier()).ifPresent(hive.honeycombs::add);
                        recalculateHoneyLevel(hive);
                    }

                    if (entity instanceof Animal animal) {
                        BeeInfoUtils.ageBee(((BeehiveBeeDataAccessor) tileBee).getTicksInHive(), animal);
                    }
                    if (entities != null) entities.add(entity);
                }
                hive.level.playSound(null, hive.worldPosition.getX(), hive.worldPosition.getY(),  hive.worldPosition.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                return hive.level.addFreshEntity(entity);
            } else {
                return true;
            }
        }
    }

    @Override
    public void addOccupantWithPresetTicks(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        if (!(bee instanceof IBeeCompat compat)) return;
        BeehiveEntityAccessor thisHive = (BeehiveEntityAccessor) this;
        if (thisHive.getBees().size() < getBlock().getTier().getMaxBees()) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);

            if (this.level != null) {
                int maxTimeInHive = (int) (compat.getMaxTimeInHive() * getBlock().getTier().getTimeModifier());
                thisHive.getBees().add(new BeeData(nbt, ticksInHive, hasNectar ? maxTimeInHive : MIN_HIVE_TIME));
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                bee.discard();
            }
        }
    }

    private TieredBeehiveBlock getBlock() {
        return (TieredBeehiveBlock) this.getBlockState().getBlock();
    }

    @Override
    public boolean isSedated() {
        assert this.level != null;
        return isSmoked || CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public static void serverSideTick(Level level, BlockPos blockPos, BlockState state, TieredBeehiveBlockEntity hive) {
        if (hive.isSmoked) {
            if (MathUtils.inRangeInclusive(hive.ticksSmoked, 0, SMOKE_TIME)) {
                hive.ticksSmoked++;
            } else {
                hive.isSmoked = false;
                hive.ticksSmoked = -1;
            }
        }

        hive.ticksSinceBeesFlagged++;
        if (hive.ticksSinceBeesFlagged == 80) {
            BeeInfoUtils.flagBeesInRange(hive.worldPosition, hive.level);
            hive.ticksSinceBeesFlagged = 0;
        }
        tickOccupants(hive, state, ((BeehiveEntityAccessor) hive).getBees());
        if (hive.hasBees() && level.getRandom().nextDouble() < 0.005D) {
            var vec = Vec3.atBottomCenterOf(blockPos);
            level.playSound(null, vec.x(), vec.y(), vec.z(), SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        DebugPackets.sendHiveInfo(level, blockPos, state, hive);
    }

    private static void tickOccupants(TieredBeehiveBlockEntity hive, BlockState state, List<BeeData> bees) {
        BeehiveBlockEntity.BeeData bee;
        for(Iterator<BeeData> iterator = bees.iterator(); iterator.hasNext(); increaseTicksInHive(bee, 1)) {
            bee = iterator.next();
            if (((BeehiveBeeDataAccessor)bee).getTicksInHive() > ((BeehiveBeeDataAccessor)bee).getMinOccupationTicks()) {
                BeehiveBlockEntity.BeeReleaseStatus status = ((BeehiveBeeDataAccessor)bee).getEntityData().getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                if (releaseBee(hive, state, bee, null, status)) {
                    iterator.remove();
                }
            }
        }
    }

    //TODO make something (item, etc) to speed up bees xD
    private static void increaseTicksInHive(BeeData beeData, int amount) {
        ((BeehiveBeeDataAccessor)beeData).setTicksInHive(((BeehiveBeeDataAccessor) beeData).getTicksInHive() + amount);
    }

    public static boolean shouldStayInHive(Level level, BeeReleaseStatus beehiveState){
        return (level != null && (level.isNight() || level.isRaining())) && beehiveState != BeeReleaseStatus.EMERGENCY;
    }

    @Override
    public boolean isFull() { return ((BeehiveEntityAccessor) this).getBees().size() >= getBlock().getTier().getMaxBees(); }

    public boolean hasBees() { return !((BeehiveEntityAccessor) this).getBees().isEmpty(); }

    public ItemStack getResourceHoneycomb(){ return honeycombs.remove(); }

    public boolean hasCombs(){ return numberOfCombs() > 0; }

    public int numberOfCombs() { return honeycombs.size(); }

    public boolean isAllowedBee(){
        return getBlockState().getBlock() instanceof TieredBeehiveBlock;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(NBTConstants.BeeHive.HONEYCOMBS)) honeycombs = getHoneycombs(nbt);
        if (nbt.contains(NBTConstants.BeeHive.SMOKED)) this.isSmoked = nbt.getBoolean(NBTConstants.BeeHive.SMOKED);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!honeycombs.isEmpty()) tag.put(NBTConstants.BeeHive.HONEYCOMBS, writeHoneycombs(honeycombs));
        tag.putBoolean(NBTConstants.BeeHive.SMOKED, isSmoked);
    }

    public ListTag writeHoneycombs(Queue<ItemStack> combs) {
        ListTag nbtTagList = new ListTag();
        for (ItemStack honeycomb : combs) nbtTagList.add(honeycomb.save(new CompoundTag()));
        return nbtTagList;
    }

    public Queue<ItemStack> getHoneycombs(CompoundTag nbt) {
        return nbt.getList(NBTConstants.BeeHive.HONEYCOMBS, Tag.TAG_COMPOUND)
                .stream()
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public Collection<ItemStack> getHoneycombs() {
        return Collections.unmodifiableCollection(honeycombs);
    }
}
