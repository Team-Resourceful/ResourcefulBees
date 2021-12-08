
package com.teamresourceful.resourcefulbees.common.tileentity;


import com.google.common.collect.Lists;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BTEBeeAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeHiveTileEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;
import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.SMOKE_TIME;

public class TieredBeehiveTileEntity extends BeehiveBlockEntity {

    private List<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = -1;
    protected int ticksSinceBeesFlagged;

    public TieredBeehiveTileEntity(BlockPos pos, BlockState state) {
        super(pos,state);
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() { return ModBlockEntityTypes.TIERED_BEEHIVE_TILE_ENTITY.get(); }


    public static void recalculateHoneyLevel() {
        float combsInHive = this.getHoneycombs().size();
        float percentValue = (combsInHive / getMaxCombs()) * 100;
        int newState = (int) Mth.clamp((percentValue - (percentValue % 20)) / 20, 0, 5) ;
        assert this.level != null;
        this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BeehiveBlock.HONEY_LEVEL, newState));
    }

    public void smokeHive() {
        this.isSmoked = true;
        ticksSmoked = ticksSmoked == -1 ? 0 : ticksSmoked;
    }

    public int getTicksSmoked() { return ticksSmoked; }

    @Override
    public void emptyAllLivingFromHive(@Nullable Player player, @NotNull BlockState state, BeehiveBlockEntity.@NotNull BeeReleaseStatus status) {
        List<Entity> list = this.releaseAllOccupants(state, status);
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

    private List<Entity> releaseAllOccupants(BlockState state, BeehiveBlockEntity.BeeReleaseStatus status) {
        List<Entity> list = Lists.newArrayList();
        this.stored.removeIf(beeData -> releaseOccupant(this.level, this.worldPosition, state, beeData, list, status));
        return list;
    }

    private static boolean releaseOccupant(Level level, BlockPos blockPos, @NotNull BlockState state, @NotNull BeehiveBlockEntity.BeeData tileBee, @Nullable List<Entity> entities, @NotNull BeeReleaseStatus beehiveState) {
        if (shouldStayInHive(beehiveState)) {
            return false;
        } else {
            CompoundTag nbt = ((BTEBeeAccessor) tileBee).getEntityData();
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Direction direction = state.getValue(BeehiveBlock.FACING);
            BlockPos relative = blockPos.relative(direction);
            if (level == null) {
                return false;
            } else {
                if (!level.getBlockState(relative).getCollisionShape(level, relative).isEmpty() && beehiveState != BeeReleaseStatus.EMERGENCY) {
                    return false;
                }
                Entity entity = EntityType.loadEntityRecursive(nbt, level, entity1 -> entity1);
                if (entity != null) {
                    BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, entity);

                    if (entity instanceof Bee vanillaBeeEntity) {
                        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);

                        if (beehiveState == BeeReleaseStatus.HONEY_DELIVERED) {
                            vanillaBeeEntity.dropOffNectar();
                            int i = getHoneyLevel(state);
                            if (i < 5) {
                                if (entity instanceof ICustomBee) {
                                    Optional<OutputVariation> outputVariation = ((ICustomBee) entity).getHoneycombData();
                                    if (outputVariation.isPresent())
                                        honeycomb = outputVariation.get().getHiveOutput(((TieredBeehiveBlock) state.getBlock()).getTier());
                                }

                                if (!honeycomb.isEmpty()) this.honeycombs.add(0, honeycomb);

                                recalculateHoneyLevel();
                            }
                        }

                        BeeInfoUtils.ageBee(((BTEBeeAccessor) tileBee).getTicksInHive(), vanillaBeeEntity);
                        if (entities != null) entities.add(entity);
                    }
                    level.playSound(null, blockPos.getX(), blockPos.getY(),  blockPos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return level.addFreshEntity(entity);
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public void addOccupantWithPresetTicks(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        BeeHiveTileEntityAccessor thisHive = (BeeHiveTileEntityAccessor) this;
        if (thisHive.getBees().size() < getBlock().getMaxBees()) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);

            if (this.level != null && bee instanceof Bee) {
                int maxTimeInHive = getMaxTimeInHive(getBlock().getTier(), bee instanceof ICustomBee ? ((ICustomBee) bee).getCoreData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
                thisHive.getBees().add(new Bee(nbt, ticksInHive,  hasNectar ? maxTimeInHive : MIN_HIVE_TIME));
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                bee.remove();
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

/*    @Override
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
    }*/

    public static boolean shouldStayInHive(Level level, BeeReleaseStatus beehiveState){
        return (level != null && (level.isNight() || level.isRaining())) && beehiveState != BeeReleaseStatus.EMERGENCY;
    }

    @Override
    public boolean isFull() { return ((BeeHiveTileEntityAccessor) this).getBees().size() >= ((TieredBeehiveBlock) this.getBlockState().getBlock()).getMaxBees(); }

    public boolean hasBees() { return !((BeeHiveTileEntityAccessor) this).getBees().isEmpty(); }

    public ItemStack getResourceHoneycomb(){ return honeycombs.remove(0); }

    public boolean hasCombs(){ return numberOfCombs() > 0; }

    public int numberOfCombs() { return getHoneycombs().size(); }

    public boolean isAllowedBee(){
        return getBlockState().getBlock() instanceof TieredBeehiveBlock;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
        if (nbt.contains(NBTConstants.NBT_HONEYCOMBS_TE)) honeycombs = getHoneycombs(nbt);
        if (nbt.contains(NBTConstants.NBT_SMOKED_TE)) this.isSmoked = nbt.getBoolean(NBTConstants.NBT_SMOKED_TE);
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        List<ItemStack> combs = getHoneycombs();
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

    public List<ItemStack> getHoneycombs() {
        return Collections.unmodifiableList(honeycombs);
    }
}
