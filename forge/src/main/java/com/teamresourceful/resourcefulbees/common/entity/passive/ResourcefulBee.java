package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.blockentity.FakeFlowerEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.entity.ai.AuraHandler;
import com.teamresourceful.resourcefulbees.common.entity.goals.*;
import com.teamresourceful.resourcefulbees.common.entity.pathfinding.BeePathNavigation;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeGoToHiveGoalInvoker;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeInvoker;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    private boolean wasColliding;
    private int numberOfMutations;
    private RBeePollinateGoal pollinateGoal;
    private int explosiveCooldown = 0;
    private final @Nullable AuraHandler auraHandler;
    private BlockPos fakeFlowerPos = null;

    public ResourcefulBee(EntityType<? extends Bee> type, Level level, String beeType) {
        super(type, level, beeType);
        //THIS NEEDS TO BE ONLY LOADED ON THE SERVER AS IT WOULD NOT MATCH HOW VANILLA LOADS GOALS OTHERWISE.
        if (level instanceof ServerLevel) registerConditionalGoals();
        auraHandler = customBeeData.getTraitData().hasAuras() ? new AuraHandler(this) : null;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());
        this.pollinateGoal = new RBeePollinateGoal(this);
        this.goalSelector.addGoal(4, this.pollinateGoal);

        ((BeeEntityAccessor) this).setPollinateGoal(new FakePollinateGoal());

        this.goalSelector.addGoal(5, new UpdateBeehiveGoal2());

        ((BeeEntityAccessor) this).setGoToHiveGoal(new FindBeehiveGoal2());
        this.goalSelector.addGoal(5, ((BeeEntityAccessor) this).getGoToHiveGoal());

        ((BeeEntityAccessor) this).setGoToKnownFlowerGoal(new BeeGoToKnownFlowerGoal());
        this.goalSelector.addGoal(6, ((BeeEntityAccessor) this).getGoToKnownFlowerGoal());
        this.goalSelector.addGoal(8, !BeeConfig.manualMode ? new BeeWanderGoal(this) : new WanderWorkerGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));
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
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D, beeType));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D));
            this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        }
        if (getMutationData().hasMutation()) {
            this.goalSelector.addGoal(4, new BeeFakeFlowerGoal(this));
            this.goalSelector.addGoal(7, new BeeMutateGoal(this));
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new BeePathNavigation(this, level, () -> !pollinateGoal.isPollinating());
    }

    public BlockPos getFakeFlowerPos() {
        return this.fakeFlowerPos;
    }

    @Override
    public boolean isHiveValid() {
        if (this.hasHive()) {
            BlockPos pos = this.getHivePos();
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                return blockEntity instanceof TieredBeehiveBlockEntity tieredHive && tieredHive.isAllowedBee()
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

    public void setExplosiveCooldown(int cooldown) {
        this.explosiveCooldown = cooldown;
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
        return hasFakeFlower() && this.level.isLoaded(this.fakeFlowerPos) && this.level.getBlockState(this.fakeFlowerPos).is(ModBlocks.FAKE_FLOWER.get());
    }

    @Override
    public boolean isFlowerValid(@NotNull BlockPos pos) {
        if (getCoreData().hasEntityFlower()) return this.level.getEntity(this.getFlowerEntityID()) != null;
        return this.level.isLoaded(pos) && this.level.getBlockState(pos).is(getCoreData().flowers());
    }

    @Override
    public void makeStuckInBlock(BlockState state, @NotNull Vec3 vector) {
        BeeTraitData info = getTraitData();
        boolean isSpider = info.hasSpecialAbilities() && info.specialAbilities().contains(TraitConstants.SPIDER);
        if (state.is(Blocks.COBWEB) && isSpider) return;
        super.makeStuckInBlock(state, vector);
    }

    @Override
    protected void customServerAiStep() {
        if (auraHandler != null && (this.tickCount + getId()) % BeeConfig.auraFrequency * 20 == 0) {
            auraHandler.tick();
        }

        BeeTraitData info = getTraitData();
        TraitAbilityRegistry registry = TraitAbilityRegistry.getRegistry();

        if (!info.hasTraits() || !info.hasSpecialAbilities()) {
            super.customServerAiStep();
            return;
        }

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
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.fakeFlowerPos = null;
        if (compound.contains("FakeFlowerPos")) {
            this.fakeFlowerPos = NbtUtils.readBlockPos(compound.getCompound("FakeFlowerPos"));
        }
        this.numberOfMutations = compound.getInt(NBTConstants.NBT_MUTATION_COUNT);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.hasFakeFlower()) {
            compound.put("FakeFlowerPos", NbtUtils.writeBlockPos(this.getFakeFlowerPos()));
        }
        compound.putInt(NBTConstants.NBT_MUTATION_COUNT, this.numberOfMutations);
    }

    public boolean hasFakeFlower() {
        return this.fakeFlowerPos != null;
    }

    public void dropFakeFlower() {
        fakeFlowerPos = null;
    }

    public void dropOffMutations() {
        this.numberOfMutations = 0;
        ((FakeFlowerEntity) level.getBlockEntity(fakeFlowerPos)).sendMutations(this);
    }

    public void setFakeFlowerPos(BlockPos clickedPos) {
        fakeFlowerPos = clickedPos;
    }

    public class EnterBeehiveGoal2 extends Bee.BeeEnterHiveGoal {

        public EnterBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (ResourcefulBee.this.hasHive() && ResourcefulBee.this.wantsToEnterHive() && ResourcefulBee.this.getHivePos() != null && ResourcefulBee.this.getHivePos().closerThan(ResourcefulBee.this.blockPosition(), 2.0D)) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.getHivePos());
                if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                    if (!beehiveBlockEntity.isFull()) {
                        return true;
                    }

                    ((BeeEntityAccessor) ResourcefulBee.this).setHivePos(null);
                } else if (blockEntity instanceof BeeHolderBlockEntity apiaryBlockEntity) {
                    if (apiaryBlockEntity.hasSpace()) {
                        return true;
                    }

                    ((BeeEntityAccessor) ResourcefulBee.this).setHivePos(null);
                }
            }

            return false;
        }

        @Override
        public void start() {
            if (ResourcefulBee.this.getHivePos() != null) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.getHivePos());
                if (blockEntity instanceof BeehiveBlockEntity hive) {
                    hive.addOccupant(ResourcefulBee.this, ResourcefulBee.this.hasNectar());
                } else if (blockEntity instanceof BeeHolderBlockEntity holder) {
                    holder.tryEnterHive(ResourcefulBee.this, ResourcefulBee.this.hasNectar(), 0);
                }
            }
        }
    }

    public class FindBeehiveGoal2 extends BeeGoToHiveGoal {


        public FindBeehiveGoal2() {
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

    public class UpdateBeehiveGoal2 extends Bee.BeeLocateHiveGoal {

        public UpdateBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return !BeeConfig.manualMode && super.canBeeUse();
        }
    }
}
