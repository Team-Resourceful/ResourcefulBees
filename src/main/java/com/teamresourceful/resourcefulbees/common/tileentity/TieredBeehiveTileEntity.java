
package com.teamresourceful.resourcefulbees.common.tileentity;


import com.google.common.collect.Lists;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveBeeDataAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;
import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.SMOKE_TIME;

public class TieredBeehiveTileEntity extends BeehiveBlockEntity {

    private final RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> entityType;
    //TODO double check that this can in fact be final
    private final List<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = -1;
    protected int ticksSinceBeesFlagged;

    public TieredBeehiveTileEntity(RegistryObject<BlockEntityType<TieredBeehiveTileEntity>> entityType, BlockPos pos, BlockState state) {
        super(pos,state);
        this.entityType = entityType;
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() { return entityType.get(); }


    public static void recalculateHoneyLevel(TieredBeehiveTileEntity hive) {
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
        List<Entity> list = this.releaseAllBees(state, status);
        if (player != null) {
            for(Entity entity : list) {
                if (entity instanceof Bee bee) {
                    if (player.position().distanceToSqr(entity.position()) <= 16.0D) {
                        if (!this.isSedated()) {
                            bee.setTarget(player);
                        } else {
                            bee.setStayOutOfHiveCountdown(400);
                        }
                    }
                }
            }
        }

    }

    private List<Entity> releaseAllBees(BlockState state, BeehiveBlockEntity.BeeReleaseStatus status) {
        List<Entity> list = Lists.newArrayList();
        ((BeehiveEntityAccessor)this).getBees().removeIf(beeData -> releaseBee(this, state, beeData, list, status));
        return list;
    }

    private static boolean releaseBee(TieredBeehiveTileEntity hive, @NotNull BlockState state, @NotNull BeehiveBlockEntity.BeeData tileBee, @Nullable List<Entity> entities, @NotNull BeeReleaseStatus beehiveState) {
        if (shouldStayInHive(hive.level, beehiveState)) {
            return false;
        } else {
            CompoundTag nbt = ((BeehiveBeeDataAccessor) tileBee).getEntityData();
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Direction direction = state.getValue(BeehiveBlock.FACING);
            BlockPos relative = hive.worldPosition.relative(direction);
            if (hive.level == null) {
                return false;
            } else {
                if (!hive.level.getBlockState(relative).getCollisionShape(hive.level, relative).isEmpty() && beehiveState != BeeReleaseStatus.EMERGENCY) {
                    return false;
                }
                Entity entity = EntityType.loadEntityRecursive(nbt, hive.level, entity1 -> entity1);
                if (entity != null) {
                    BeeInfoUtils.setEntityLocationAndAngle(hive.worldPosition, direction, entity);

                    if (entity instanceof Bee vanillaBeeEntity) {
                        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);

                        if (beehiveState == BeeReleaseStatus.HONEY_DELIVERED) {
                            vanillaBeeEntity.dropOffNectar();
                            int i = getHoneyLevel(state);
                            if (i < 5) {
                                if (entity instanceof ICustomBee) {
                                    Optional<OutputVariation> outputVariation = ((ICustomBee) entity).getHoneycombData();
                                    if (outputVariation.isPresent())
                                        honeycomb = outputVariation.get().getHiveOutput(((TieredBeehiveBlock) state.getBlock()).getTier().ordinal());
                                }

                                if (!honeycomb.isEmpty()) hive.honeycombs.add(0, honeycomb);

                                recalculateHoneyLevel(hive);
                            }
                        }

                        BeeInfoUtils.ageBee(((BeehiveBeeDataAccessor) tileBee).getTicksInHive(), vanillaBeeEntity);
                        if (entities != null) entities.add(entity);
                    }
                    hive.level.playSound(null, hive.worldPosition.getX(), hive.worldPosition.getY(),  hive.worldPosition.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return hive.level.addFreshEntity(entity);
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public void addOccupantWithPresetTicks(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        BeehiveEntityAccessor thisHive = (BeehiveEntityAccessor) this;
        if (thisHive.getBees().size() < getBlock().getTier().getMaxBees()) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);

            if (this.level != null && bee instanceof Bee) {
                //TODO fix getMaxTimeInHive after ApiaryTier Extensible Enum is created
                int maxTimeInHive = getMaxTimeInHive(getBlock().getTier().ordinal(), bee instanceof ICustomBee ? ((ICustomBee) bee).getCoreData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
                thisHive.getBees().add(new BeeData(nbt, ticksInHive,  hasNectar ? maxTimeInHive : MIN_HIVE_TIME));
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                bee.discard();
            }
        }
    }

    private TieredBeehiveBlock getBlock() {
        return (TieredBeehiveBlock) this.getBlockState().getBlock();
    }

    private static int getMaxTimeInHive(int tier, int timeInput) {

        if (tier != 1) {
            if (tier == 0) {
                return (int) (timeInput * 1.05);
            } else {
                return (int) (timeInput * (1 - tier * .05));
            }
        }
        return timeInput;
    }

    @Override
    public boolean isSedated() {
        assert this.level != null;
        return isSmoked || CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    public static void serverSideTick(Level level, BlockPos blockPos, BlockState state, TieredBeehiveTileEntity hive) {
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
            double d0 = blockPos.getX() + 0.5D;
            double d1 = blockPos.getY();
            double d2 = blockPos.getZ() + 0.5D;
            level.playSound(null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        DebugPackets.sendHiveInfo(level, blockPos, state, hive);
    }

    private static void tickOccupants(TieredBeehiveTileEntity hive, BlockState state, List<BeeData> bees) {
        BeehiveBlockEntity.BeeData bee;
        for(Iterator<BeeData> iterator = bees.iterator(); iterator.hasNext(); ((BeehiveBeeDataAccessor)bee).getTicksInHive()) {
            bee = iterator.next();
            if (((BeehiveBeeDataAccessor)bee).getTicksInHive() > ((BeehiveBeeDataAccessor)bee).getMinOccupationTicks()) {
                BeehiveBlockEntity.BeeReleaseStatus status = ((BeehiveBeeDataAccessor)bee).getEntityData().getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                if (releaseBee(hive, state, bee, null, status)) {
                    iterator.remove();
                }
            }
        }
    }

    public static boolean shouldStayInHive(Level level, BeeReleaseStatus beehiveState){
        return (level != null && (level.isNight() || level.isRaining())) && beehiveState != BeeReleaseStatus.EMERGENCY;
    }

    @Override
    public boolean isFull() { return ((BeehiveEntityAccessor) this).getBees().size() >= getBlock().getTier().getMaxBees(); }

    public boolean hasBees() { return !((BeehiveEntityAccessor) this).getBees().isEmpty(); }

    public ItemStack getResourceHoneycomb(){ return honeycombs.remove(0); }

    public boolean hasCombs(){ return numberOfCombs() > 0; }

    public int numberOfCombs() { return honeycombs.size(); }

    public boolean isAllowedBee(){
        return getBlockState().getBlock() instanceof TieredBeehiveBlock;
    }

/*    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains(NBTConstants.NBT_HONEYCOMBS_TE)) honeycombs = getHoneycombs(nbt);
        if (nbt.contains(NBTConstants.NBT_SMOKED_TE)) this.isSmoked = nbt.getBoolean(NBTConstants.NBT_SMOKED_TE);
    }*/

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        List<ItemStack> combs = honeycombs;
        if (!combs.isEmpty()) nbt.put(NBTConstants.NBT_HONEYCOMBS_TE, writeHoneycombs(combs));
        nbt.putBoolean(NBTConstants.NBT_SMOKED_TE, isSmoked);
        return nbt;
    }

    public ListTag writeHoneycombs(List<ItemStack> combs) {
        ListTag nbtTagList = new ListTag();
        for (ItemStack honeycomb : combs) nbtTagList.add(honeycomb.save(new CompoundTag()));
        return nbtTagList;
    }

    public List<ItemStack> getHoneycombs(CompoundTag nbt) {
        List<ItemStack> itemStacks = new LinkedList<>();
        ListTag tagList = nbt.getList(NBTConstants.NBT_HONEYCOMBS_TE, Tag.TAG_COMPOUND);
        tagList.forEach(compound -> itemStacks.add(0, ItemStack.of((CompoundTag) compound)));
        return itemStacks;
    }

    /*public List<ItemStack> getHoneycombs() {
        return Collections.unmodifiableList(honeycombs);
    }*/
}
