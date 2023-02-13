package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.entities.ai.AuraHandler;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entities.goals.*;
import com.teamresourceful.resourcefulbees.common.entities.pathfinding.BeePathNavigation;
import com.teamresourceful.resourcefulbees.common.entity.goals.BeeFakeFlowerGoal;
import com.teamresourceful.resourcefulbees.common.entity.goals.BeeMutateGoal;
import com.teamresourceful.resourcefulbees.common.entity.goals.ModBeePollinateGoal;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeGoToHiveGoalInvoker;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeInvoker;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.util.SerializedDataEntry;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class ResourcefulBee extends CustomBeeEntity {

    private final @Nullable AuraHandler auraHandler;
    public final SerializedDataEntry<BlockPos, CompoundTag> fakeFlower = SerializedDataEntry.Builder
        .of(NBTConstants.FAKE_FLOWER_POS, CompoundTag.TYPE, (BlockPos) null)
        .withWriter(NbtUtils::writeBlockPos)
        .withReader(NbtUtils::readBlockPos)
        .build();
    public final SerializedDataEntry<Integer, IntTag> entityFlower = SerializedDataEntry.Builder
        .of("", IntTag.TYPE, (Integer) null)
        .build();

    private boolean wasColliding;
    private int numberOfMutations;
    private ModBeePollinateGoal pollinateGoal;
    private int explosiveCooldown = 0;

    public ResourcefulBee(EntityType<? extends Bee> type, Level level, String beeType) {
        super(type, level, beeType);
        //THIS NEEDS TO BE ONLY LOADED ON THE SERVER AS IT WOULD NOT MATCH HOW VANILLA LOADS GOALS OTHERWISE.
        if (level instanceof ServerLevel) registerConditionalGoals();
        auraHandler = customBeeData.getTraitData().hasAuras() ? new AuraHandler(this, this.getBeeData()) : null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ModBeeEnterHiveGoal(this));
        this.pollinateGoal = new ModBeePollinateGoal(this);
        this.goalSelector.addGoal(4, this.pollinateGoal);

        BeeEntityAccessor accessor = (BeeEntityAccessor) this;

        accessor.setPollinateGoal(new FakePollinateGoal());

        this.goalSelector.addGoal(6, new Bee.BeeLocateHiveGoal() {
            @Override
            public boolean canBeeUse() {
                return !BeeConfig.manualMode && super.canBeeUse();
            }
        });

        accessor.setGoToHiveGoal(new GoToHiveGoal());
        this.goalSelector.addGoal(6, accessor.getGoToHiveGoal());

        accessor.setGoToKnownFlowerGoal(new BeeGoToKnownFlowerGoal());
        this.goalSelector.addGoal(7, accessor.getGoToKnownFlowerGoal());
        this.goalSelector.addGoal(9, !BeeConfig.manualMode ? new BeeWanderGoal(this) : new WanderWorkerGoal(this));
        this.goalSelector.addGoal(10, new FloatGoal(this));
    }

    /**
     * This method is used to register goals that require data passed into the constructor, and should only be used for that.
     * This is required because vanilla calls registerGoals() in the mob constructor so we can't get any of the data we pass in through the constructor.
     * <br>
     * <br>
     * Vanilla falls into this problem themself with the fox and rabbit to and have a similar method.
     * The only difference is they call theirs on finalize spawn because they choose a type on spawn but we choose it before in construction and pass it in.
     * But the premise still apply that they wait because they need data that would not be available in the normal registerGoals method.
     * <br>
     * See {@link net.minecraft.world.entity.animal.Fox#setTargetGoals()}
     */
    @SuppressWarnings("JavadocReference")
    protected void registerConditionalGoals() {

        if (!getCombatData().isPassive()) {
            this.goalSelector.addGoal(0, new Bee.BeeAttackGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setAlertOthers());
            this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));
        }
        if (getBreedData().hasParents()) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D, getBeeType()));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D));
            this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.25D));
        }
        if (getMutationData().hasMutation(level)) {
            this.goalSelector.addGoal(5, new BeeFakeFlowerGoal(this));
            this.goalSelector.addGoal(8, new BeeMutateGoal(this));
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new BeePathNavigation(this, level, () -> !pollinateGoal.isPollinating());
    }

    @Override
    public boolean isHiveValid() {
        if (this.hasHive()) {
            BlockPos pos = this.getHivePos();
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                return blockEntity instanceof TieredBeehiveBlockEntity hive && hive.isAllowedBee()
                        || blockEntity instanceof BeeHolderBlockEntity apiary && apiary.isAllowedBee()
                        || blockEntity instanceof BeehiveBlockEntity;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (explosiveCooldown > 0) explosiveCooldown--;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (getTarget() != null && getTraitData().damageTypes().stream().anyMatch(damageType -> damageType.type().equals(TraitConstants.EXPLOSIVE))) {
            this.explosiveCooldown = 60;
        }
    }

    @Override
    public void dropOffNectar() {
        super.dropOffNectar();
        this.resetCropCounter();
    }

    @Override
    public boolean wantsToEnterHive() {
        if (((BeeEntityAccessor) this).getStayOutOfHiveCountdown() <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = ((BeeInvoker) this).callIsTiredOfLookingForNectar() || this.hasNectar();
            return flag && !((BeeInvoker) this).callIsHiveNearFire();
        }
        return false;
    }

    private void resetCropCounter() {
        this.numberOfMutations = 0;
    }

    @Override
    public void incrementNumCropsGrownSincePollination() {
        ++this.numberOfMutations;
    }

    public int getNumberOfMutations() {
        return this.numberOfMutations;
    }

    public boolean isFakeFlowerValid() {
        return this.fakeFlower.hasData() && WorldUtils.checkBlock(this.level, this.fakeFlower.get(), state -> state.is(ModBlocks.FAKE_FLOWER.get()));
    }

    @Override
    public boolean isFlowerValid(@NotNull BlockPos pos) {
        if (getCoreData().hasEntityFlower()) {
            return this.entityFlower.hasData() && this.level.getEntity(this.entityFlower.get()) != null;
        }
        return WorldUtils.checkBlock(this.level, pos, state -> state.is(getCoreData().flowers()));
    }

    @Override
    public void makeStuckInBlock(BlockState state, @NotNull Vec3 vector) {
        BeeTraitData info = getTraitData();
        boolean isSpider = info.hasSpecialAbilities() && info.specialAbilities().contains(TraitConstants.SPIDER);
        if (state.is(Blocks.COBWEB) && isSpider || state.is(Blocks.SWEET_BERRY_BUSH)) return;
        super.makeStuckInBlock(state, vector);
    }

    @Override
    protected void customServerAiStep() {
        if (auraHandler != null && (this.tickCount + getId()) % BeeConfig.auraFrequency * 20 == 0) {
            auraHandler.tick();
        }

        BeeTraitData info = getTraitData();

        if (!info.hasTraits() || !info.hasSpecialAbilities()) {
            super.customServerAiStep();
            return;
        }

        TraitAbilityRegistry registry = TraitAbilityRegistry.get();

        info.specialAbilities().stream()
            .map(registry::getAbility)
            .filter(Objects::nonNull)
            .filter(TraitAbility::canRun)
            .forEach(ability -> ability.run(this));

        super.customServerAiStep();
    }

    public void setColliding() {
        wasColliding = true;
    }

    public boolean wasColliding() {
        return wasColliding;
    }

    public boolean isPollinating() {
        return this.pollinateGoal.isPollinating();
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (entity.hurt(DamageSource.sting(this), damage)) {
            this.doEnchantDamageEffects(this, entity);
            if (entity instanceof LivingEntity target) {
                target.setStingerCount(target.getStingerCount() + 1);
                applyTraitEffectsAndDamage(target, getDifficultyModifier());
            }

            this.stopBeingAngry();
            ((BeeInvoker) this).callSetFlag(4, BeeConfig.beesDieFromSting && this.getCombatData().removeStingerOnAttack());
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    private void applyTraitEffectsAndDamage(@NotNull LivingEntity target, int diffMod) {
        BeeTraitData info = getTraitData();
        if (info.hasTraits() && diffMod > 0) {
            if (info.hasDamageTypes()) {
                info.damageTypes().forEach(damageType -> {
                    if (damageType.type().equals(TraitConstants.SET_ON_FIRE))
                        target.setSecondsOnFire(diffMod * damageType.amplifier());
                    if (damageType.type().equals(TraitConstants.EXPLOSIVE))
                        this.explode(diffMod / damageType.amplifier());
                });
            }
            int duration = diffMod * 20;
            if (info.hasPotionDamageEffects()) {
                info.potionDamageEffects().forEach(effect -> target.addEffect(effect.createInstance(duration)));
            }
            if (canPoison(info)) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, 0));
            }
        }
    }

    private int getDifficultyModifier() {
        return switch (this.level.getDifficulty()) {
            case EASY -> 5;
            case NORMAL -> 10;
            case HARD -> 18;
            case PEACEFUL -> 0;
        };
    }

    private boolean canPoison(BeeTraitData info) {
        return BeeConfig.beesInflictPoison && this.getCombatData().inflictsPoison() && info.canPoison();
    }

    private void explode(int radius) {
        if (!this.level.isClientSide) {
            Explosion.BlockInteraction mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), random.nextFloat() * radius, explosiveCooldown > 0 ? Explosion.BlockInteraction.NONE : mode);
            this.discard();
            this.spawnLingeringCloud(this.getActiveEffects().stream().map(MobEffectInstance::new).toList());
        }
    }

    private void spawnLingeringCloud(Collection<MobEffectInstance> effects) {
        if (!effects.isEmpty()) {
            AreaEffectCloud cloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            cloud.setRadius(2.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setDuration(cloud.getDuration() / 2);
            cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
            effects.forEach(cloud::addEffect);

            this.level.addFreshEntity(cloud);
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.fakeFlower.read(tag);
        this.numberOfMutations = tag.getInt(NBTConstants.NBT_MUTATION_COUNT);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.fakeFlower.save(compound);
        compound.putInt(NBTConstants.NBT_MUTATION_COUNT, getNumberOfMutations());
    }

    public void dropOffMutations() {
        this.numberOfMutations = getMutationData().count();
        if (isFakeFlowerValid()) {
            level.getBlockEntity(fakeFlower.get(), ModBlockEntityTypes.FAKE_FLOWER_ENTITY.get())
                .ifPresent(entity -> entity.createPollen(this));
        }
    }

    public class GoToHiveGoal extends BeeGoToHiveGoal {

        public GoToHiveGoal() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return getHivePos() != null && !hasRestriction() && wantsToEnterHive() && !((BeeGoToHiveGoalInvoker) this).callHasReachedTarget(getHivePos()) && isHiveValid();
        }

        @Override
        public void dropHive() {
            if (!BeeConfig.manualMode) {
                super.dropHive();
            }  // double check blacklist as it may need to be cleared - epic
        }
    }

    public class FakePollinateGoal extends Bee.BeePollinateGoal {

        public FakePollinateGoal() {
            super();
        }

        @Override
        public boolean isPollinating() {
            return pollinateGoal.isPollinating();
        }

        @Override
        public void stopPollinating() {
            pollinateGoal.stopPollinating();
        }
    }
}
