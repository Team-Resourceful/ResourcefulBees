package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.goals.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeEntityInvoker;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.FindBeehiveGoalInvoker;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitAbilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class ResourcefulBee extends CustomBeeEntity {
    private boolean wasColliding;
    private int numberOfMutations;
    private RBeePollinateGoal pollinateGoal;
    private int explosiveCooldown = 0;

    public ResourcefulBee(EntityType<? extends Bee> type, Level world, String beeType) {
        super(type, world, beeType);
    }

    @Override
    protected void registerGoals() {
        //this method is called before constructor finishes making passed data impossible to access.
        String namespaceID = this.getEncodeId();
        assert namespaceID != null;
        String beeType = namespaceID.substring(namespaceID.lastIndexOf(":") + 1, namespaceID.length() - 4);
        CustomBeeData customBeeData = BeeRegistry.getRegistry().getBeeData(beeType);
        boolean isPassive = customBeeData.getCombatData().isPassive();
        boolean isBreedable = customBeeData.getBreedData().hasParents();
        boolean hasMutation = customBeeData.getMutationData().hasMutation();

        if (!isPassive) {
            this.goalSelector.addGoal(0, new Bee.BeeAttackGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setAlertOthers());
            this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));
        }
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());

        if (isBreedable) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D, beeType));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D));
            this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        }
        this.pollinateGoal = new RBeePollinateGoal(this);
        this.goalSelector.addGoal(4, this.pollinateGoal);

        ((BeeEntityAccessor)this).setPollinateGoal(new FakePollinateGoal());

        this.goalSelector.addGoal(5, new ResourcefulBee.UpdateBeehiveGoal2());

        ((BeeEntityAccessor)this).setGoToHiveGoal(new ResourcefulBee.FindBeehiveGoal2());
        this.goalSelector.addGoal(5, ((BeeEntityAccessor)this).getGoToHiveGoal());

        ((BeeEntityAccessor)this).setGoToKnownFlowerGoal(new Bee.BeeGoToKnownFlowerGoal());
        this.goalSelector.addGoal(6, ((BeeEntityAccessor)this).getGoToKnownFlowerGoal());

        if (hasMutation) {
            this.goalSelector.addGoal(7, new BeeMutateGoal(this));
        }
        if (Boolean.FALSE.equals(CommonConfig.MANUAL_MODE.get())) this.goalSelector.addGoal(8, new BeeWanderGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));
    }

    @Override
    public boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            BlockPos pos = this.getHivePos();
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                return blockEntity instanceof TieredBeehiveBlockEntity && ((TieredBeehiveBlockEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof ApiaryBlockEntity && ((ApiaryBlockEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof BeehiveBlockEntity;
            } else
                return false;
        }
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
        if (((BeeEntityAccessor)this).getStayOutOfHiveCountdown() <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = ((BeeEntityInvoker)this).callIsTiredOfLookingForNectar() || this.hasNectar();
            return flag && !((BeeEntityInvoker)this).callIsHiveNearFire();
        } else {
            return false;
        }
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

    @Override
    public boolean isFlowerValid(@NotNull BlockPos pos) {
        if (getCoreData().getEntityFlower().isPresent()) {
            return this.level.getEntity(this.getFlowerEntityID()) != null;
        } else if (!getCoreData().getBlockFlowers().isEmpty()) {
            return this.level.isLoaded(pos) && getCoreData().getBlockFlowers().contains(this.level.getBlockState(pos).getBlock());
        }
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState state, @NotNull Vec3 vector) {
        TraitData info = getTraitData();
        boolean isSpider = info.hasSpecialAbilities() && info.getSpecialAbilities().contains(TraitConstants.SPIDER);
        if (state.is(Blocks.COBWEB) && isSpider) return;
        super.makeStuckInBlock(state, vector);
    }

    @Override
    protected void customServerAiStep() {
        TraitData info = getTraitData();
        TraitAbilityRegistry registry = TraitAbilityRegistry.getRegistry();

        if (!info.hasTraits() || !info.hasSpecialAbilities()) {
            super.customServerAiStep();
            return;
        }

        info.getSpecialAbilities().stream()
                .map(registry::getAbility)
                .filter(Objects::nonNull)
                .forEach(consumer -> consumer.accept(this));

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
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        TraitData info = getTraitData();
        boolean flag = entityIn.hurt(DamageSource.sting(this), damage);
        if (flag) {
            this.doEnchantDamageEffects(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).setStingerCount(((LivingEntity) entityIn).getStingerCount() + 1);
                int i = getDifficultyModifier();
                if (info.hasTraits() && info.hasDamageTypes()) {
                    info.getDamageTypes().forEach(damageType -> {
                        if (damageType.getType().equals(TraitConstants.SET_ON_FIRE))
                            entityIn.setSecondsOnFire(i * damageType.getAmplifier());
                        if (damageType.getType().equals(TraitConstants.EXPLOSIVE))
                            this.explode(i / damageType.getAmplifier());
                    });
                }
                if (i > 0 && info.hasTraits() && info.hasPotionDamageEffects()) {
                    info.getPotionDamageEffects().forEach(damageEffect -> ((LivingEntity) entityIn).addEffect(new MobEffectInstance(damageEffect.getEffect(), i * 20, damageEffect.getStrength())));
                }
                if (canPoison(info))
                    ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0));
            }

            this.stopBeingAngry();
            ((BeeEntityInvoker) this).callSetFlag(4, CommonConfig.BEE_DIES_FROM_STING.get() && this.getCombatData().removeStingerOnAttack());
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    private int getDifficultyModifier() {
        return switch (this.level.getDifficulty()) {
            case EASY -> 5;
            case NORMAL -> 10;
            case HARD -> 18;
            default -> 0;
        };
    }

    private boolean canPoison(TraitData info) {
        return (CommonConfig.BEES_INFLICT_POISON.get() && this.getCombatData().inflictsPoison()) && info.hasTraits() && !info.hasPotionDamageEffects() && !info.hasDamageTypes();
    }

    private void explode(int radius) {
        if (!this.level.isClientSide) {
            Explosion.BlockInteraction mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), random.nextFloat() * radius, explosiveCooldown > 0 ? Explosion.BlockInteraction.NONE : mode);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
            }

            this.level.addFreshEntity(areaeffectcloudentity);
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.numberOfMutations = compound.getInt(NBTConstants.NBT_MUTATION_COUNT);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NBTConstants.NBT_MUTATION_COUNT, this.numberOfMutations);
    }

    public class EnterBeehiveGoal2 extends Bee.BeeEnterHiveGoal {
        public EnterBeehiveGoal2() {
            super();
            //constructor
        }

        @Override
        public boolean canBeeUse() {
            if (ResourcefulBee.this.hasHive() && ResourcefulBee.this.wantsToEnterHive() && ResourcefulBee.this.getHivePos() != null && ResourcefulBee.this.getHivePos().closerThan(ResourcefulBee.this.position(), 2.0D)) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.getHivePos());
                if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                    if (!beehiveBlockEntity.isFull()) {
                        return true;
                    }

                    ((BeeEntityAccessor)ResourcefulBee.this).setHivePos(null);
                } else if (blockEntity instanceof ApiaryBlockEntity apiaryBlockEntity) {
                    if (apiaryBlockEntity.hasSpace()) {
                        return true;
                    }

                    ((BeeEntityAccessor)ResourcefulBee.this).setHivePos(null);
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            if (ResourcefulBee.this.getHivePos() != null) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.getHivePos());
                if (blockEntity != null) {
                    if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                        beehiveBlockEntity.addOccupant(ResourcefulBee.this, ResourcefulBee.this.hasNectar());
                    } else if (blockEntity instanceof ApiaryBlockEntity apiaryBlockEntity) {
                        apiaryBlockEntity.tryEnterHive(ResourcefulBee.this, ResourcefulBee.this.hasNectar(), 0);
                    }
                }
            }
        }
    }

    public class FindBeehiveGoal2 extends Bee.BeeGoToHiveGoal {

        public FindBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return getHivePos() != null && !hasRestriction() && wantsToEnterHive() && !((FindBeehiveGoalInvoker)this).callHasReachedTarget(getHivePos()) && isHiveValid();
        }

        @Override
        public void dropHive() {
            if (Boolean.FALSE.equals(CommonConfig.MANUAL_MODE.get())) {
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
            if (Boolean.FALSE.equals(CommonConfig.MANUAL_MODE.get())) {
                return super.canBeeUse();
            }
            return false;
        }
    }
}
