
package com.teamresourceful.resourcefulbees.common.blockentities;


import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefulbees.mixin.common.BeehiveBeeDataAccessor;
import com.teamresourceful.resourcefulbees.mixin.common.BeehiveEntityAccessor;
import com.teamresourceful.resourcefullib.common.caches.CacheableFunction;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;
import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.SMOKE_TIME;

public class TieredBeehiveBlockEntity extends BeehiveBlockEntity implements SmokeableHive {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CacheableFunction<Block, BlockEntityType<?>> HIVE_TO_ENTITY = new CacheableFunction<>(block ->
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES
            .getEntries()
            .stream()
            .map(RegistryEntry::get)
            .filter(type -> type.isValid(block.defaultBlockState()))
            .findFirst()
            .orElse(null)
    );

    private Queue<ItemStack> honeycombs = new LinkedList<>();
    protected boolean isSmoked = false;
    protected int ticksSmoked = -1;
    protected int ticksSinceBeesFlagged;

    public TieredBeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() {
       return ModBlockEntityTypes.TIERED_BEEHIVE_ENTITY.get();
    }

    /**
     * This is a hack to fix an issue where mod devs are using the gather capabilities event and trying to get the block entity type
     * we can not pass the type to the other constructor because it is not available because the bee hive already sets it, so we override it.
     */
    private BlockEntityType<?> getEntityType() {
        return HIVE_TO_ENTITY.apply(getBlockState().getBlock());
    }


    public static void recalculateHoneyLevel(TieredBeehiveBlockEntity hive) {
        float combsInHive = hive.honeycombs.size();
        float percentValue = (combsInHive / hive.getBlock().getTier().maxCombs()) * 100;
        int newState = (int) Mth.clamp((percentValue - (percentValue % 20)) / 20, 0, 5);
        if (hive.level != null) {
            hive.level.setBlockAndUpdate(hive.worldPosition, hive.getBlockState().setValue(BeehiveBlock.HONEY_LEVEL, newState));
        }
    }

    @Override
    public void smokeHive() {
        this.isSmoked = true;
        ticksSmoked = ticksSmoked == -1 ? 0 : ticksSmoked;
    }

    public int getTicksSmoked() {
        return ticksSmoked;
    }

    @Override
    public void emptyAllLivingFromHive(@Nullable Player player, @NotNull BlockState state, @NotNull BeeReleaseStatus status) {
        if (player == null) return;
        this.releaseAllBees(state, status)
                .stream()
                .filter(e -> e.position().distanceToSqr(player.position()) <= 16.0D)
                .forEach(entity -> {
                    if (!this.isSedated()) {
                        if (entity instanceof Mob mob) {
                            mob.setTarget(player);
                        } else if (entity instanceof BeeCompat compat) {
                            compat.resourcefulBees$setOutOfHiveCooldown(400);
                        }
                    }
                });
    }

    private List<Entity> releaseAllBees(BlockState state, BeeReleaseStatus status) {
        List<Entity> list = Lists.newArrayList();
        getBees().removeIf(beeData -> releaseBee(this, state, beeData, list, status));
        return list;
    }

    private static boolean releaseBee(TieredBeehiveBlockEntity hive, @NotNull BlockState state, @NotNull BeehiveBlockEntity.BeeData beeData, @Nullable List<Entity> entities, @NotNull BeeReleaseStatus releaseStatus) {
        Level level = hive.level;
        BlockPos hivePos = hive.worldPosition;
        if (level == null) return false;
        if (shouldStayInHive(level, releaseStatus, hivePos)) {
            return false;
        } else {
            Direction facing = state.getValue(BeehiveBlock.FACING);
            BlockPos facingPos = hivePos.relative(facing);

            if (!level.getBlockState(facingPos).getCollisionShape(level, facingPos).isEmpty() && releaseStatus != BeeReleaseStatus.EMERGENCY) {
                return false;
            }
            Entity entity = beeData.toOccupant().createEntity(level, hive.getBlockPos());//EntityType.loadEntityRecursive(nbt, hive.level, entity1 -> entity1);
            if (entity != null) {
                EntityUtils.setEntityLocationAndAngle(hive.worldPosition, facing, entity);
                if (releaseStatus == BeeReleaseStatus.HONEY_DELIVERED) {
                    if (entity instanceof BeeCompat compat) compat.resourcefulBees$nectarDroppedOff();
                    if (getHoneyLevel(state) < 5) {

                        HiveRecipe.getHiveOutput(hive.getBlock().getTier(), entity)
                            .filter(Predicate.not(ItemStack::isEmpty))
                            .ifPresent(hive.honeycombs::add);
                        recalculateHoneyLevel(hive);
                    }

                    //if (entity instanceof Animal animal) {
                   //     EntityUtils.ageBee(((BeehiveBeeDataAccessor) beeData).getTicksInHive(), animal);
                    //}
                    if (entities != null) entities.add(entity);
                }
                hive.level.playSound(null, hive.worldPosition, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                return hive.level.addFreshEntity(entity);
            }
            return true;
        }
    }

    @Override
    public void addOccupant(@NotNull Bee bee) {
        if (!(bee instanceof BeeCompat compat)) return;
        if (getOccupantCount() < getBlock().getTier().maxBees()) {
            bee.stopRiding();
            bee.ejectPassengers();
            bee.dropLeash();
            //CompoundTag nbt = new CompoundTag();
            //bee.save(nbt);

            if (this.level != null) {
                getBees().add(new BeeData(occupantOf(bee, compat, this)));
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(bee, this.getBlockState()));
                bee.discard();
                super.setChanged();
            }
        }
    }

    //todo try to reduce these to a single occupantOf method
    private static BeehiveBlockEntity.Occupant occupantOf(Entity entity, BeeCompat compat, TieredBeehiveBlockEntity hive) {
        BeehiveBlockEntity.Occupant occupant;
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);
            CompoundTag entityTag = output.buildResult();
            boolean hasNectar = entityTag.getBooleanOr("HasNectar", false);
            int maxTimeInHive = (int) (compat.resourcefulBees$getMaxTimeInHive() * hive.getBlock().getTier().timeModifier());
            occupant = new BeehiveBlockEntity.Occupant(TypedEntityData.of(entity.getType(), entityTag), 0, hasNectar ? maxTimeInHive : MIN_HIVE_TIME);
        }

        return occupant;
    }

    public static BeehiveBlockEntity.Occupant occupantOf(CustomBeeEntityType<?> entity, WorldGenLevel level, int timeInHive, TieredBeehiveBlockEntity hive) {
        Entity bee = EntityType.loadEntityRecursive(entity, new CompoundTag(), level.getLevel(), EntitySpawnReason.CHUNK_GENERATION, EntityProcessor.NOP);
        BeehiveBlockEntity.Occupant occupant;
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(bee.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, bee.registryAccess());
            bee.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);
            int maxTimeInHive = (int) (timeInHive * hive.getBlock().getTier().timeModifier());
            occupant = new BeehiveBlockEntity.Occupant(TypedEntityData.of(entity, output.buildResult()), 0, maxTimeInHive);
        }

        return occupant;
    }

    private TieredBeehiveBlock getBlock() {
        return (TieredBeehiveBlock) this.getBlockState().getBlock();
    }

    @Override
    public boolean isSedated() {
        return isSmoked || super.isSedated();
    }

    public static void serverSideTick(Level level, BlockPos pos, BlockState state, TieredBeehiveBlockEntity hive) {
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
            EntityUtils.flagBeesInRange(pos, level);
            hive.ticksSinceBeesFlagged = 0;
        }
        tickOccupants(hive, state, hive.getBees());
        if (hive.hasBees() && level.getRandom().nextDouble() < 0.005D) {
            var vec = Vec3.atBottomCenterOf(pos);
            level.playSound(null, vec.x(), vec.y(), vec.z(), SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        //DebugPackets.sendHiveInfo(level, pos, state, hive);
    }

    private static void tickOccupants(TieredBeehiveBlockEntity hive, BlockState state, List<BeeData> bees) {
        BeehiveBlockEntity.BeeData bee;
        for (Iterator<BeeData> iterator = bees.iterator(); iterator.hasNext(); increaseTicksInHive(bee, 1)) {
            bee = iterator.next();
            if (bee.tick()) {
                BeeReleaseStatus status = bee.hasNectar() ? BeeReleaseStatus.HONEY_DELIVERED : BeeReleaseStatus.BEE_RELEASED;
                if (releaseBee(hive, state, bee, null, status)) {
                    iterator.remove();
                }
            }
        }
    }

    //TODO make something (item, etc) to speed up bees xD
    @SuppressWarnings("SameParameterValue")
    private static void increaseTicksInHive(BeeData beeData, int amount) {
        ((BeehiveBeeDataAccessor) beeData).setTicksInHive(((BeehiveBeeDataAccessor) beeData).getTicksInHive() + amount);
    }

    public static boolean shouldStayInHive(Level level, BeeReleaseStatus releaseStatus, BlockPos blockPos) {
        return level.environmentAttributes().getValue(EnvironmentAttributes.BEES_STAY_IN_HIVE, blockPos) && releaseStatus != BeeReleaseStatus.EMERGENCY;
    }

    @Override
    public boolean isFull() {
        return getOccupantCount() >= getBlock().getTier().maxBees();
    }

    public boolean hasBees() {
        return !this.isEmpty();
    }

    public List<BeeData> getBees() {
        return ((BeehiveEntityAccessor) this).getStored();
    }

    public ItemStack getResourceHoneycomb() {
        return honeycombs.remove();
    }

    public boolean hasCombs() {
        return numberOfCombs() > 0;
    }

    public int numberOfCombs() {
        return honeycombs.size();
    }

    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof TieredBeehiveBlock;
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        isSmoked = input.getBooleanOr(NBTConstants.BeeHive.SMOKED, false);
        honeycombs = input.listOrEmpty(NBTConstants.BeeHive.HONEYCOMBS, ItemStack.CODEC)
                .stream().collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(NBTConstants.BeeHive.SMOKED, isSmoked);
        ValueOutput.TypedOutputList<ItemStack> outputList = output.list(NBTConstants.BeeHive.HONEYCOMBS, ItemStack.CODEC);
        for (ItemStack honeycomb : honeycombs) outputList.add(honeycomb);
    }

    public Collection<ItemStack> getHoneycombs() {
        return Collections.unmodifiableCollection(honeycombs);
    }
}
