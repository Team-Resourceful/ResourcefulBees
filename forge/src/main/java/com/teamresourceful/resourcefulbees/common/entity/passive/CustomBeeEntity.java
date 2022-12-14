package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CustomBeeEntity extends Bee implements CustomBee, IAnimatable, BeeCompat {

    private static final EntityDataAccessor<Integer> FEED_COUNT = SynchedEntityData.defineId(CustomBeeEntity.class, EntityDataSerializers.INT);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    protected final CustomBeeData customBeeData;
    protected int timeWithoutHive;
    private boolean hasHiveInRange;
    private int disruptorInRange;

    public CustomBeeEntity(EntityType<? extends Bee> type, Level world, String beeType) {
        super(type, world);
        this.customBeeData = BeeRegistry.get().getBeeData(beeType);
    }

    //region BEE INFO RELATED METHODS BELOW

    @Override
    public CustomBeeData getBeeData() {
        return customBeeData;
    }

    @Override
    public int getFeedCount() {
        return this.entityData.get(FEED_COUNT);
    }

    @Override
    public void resetFeedCount() {
        this.entityData.set(FEED_COUNT, 0);
    }

    @Override
    public void addFeedCount() {
        this.entityData.set(FEED_COUNT, this.getFeedCount() + 1);
    }
    //endregion

    //region CUSTOM BEE RELATED METHODS BELOW

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        if (getCombatData().isInvulnerable() && !source.equals(DamageSource.OUT_OF_WORLD)) return true;

        BeeTraitData info = getTraitData();
        if (hasEffect(MobEffects.WATER_BREATHING) && source == DamageSource.DROWN) {
            return true;
        }
        if (source.equals(DamageSource.SWEET_BERRY_BUSH)) {
            return true;
        }
        if (info.hasTraits() && info.hasDamageImmunities() && info.damageImmunities().stream().anyMatch(source.msgId::equalsIgnoreCase)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effectInstance) {
        BeeTraitData info = getTraitData();
        if (info.hasTraits() && info.hasPotionImmunities()) {
            MobEffect potionEffect = effectInstance.getEffect();
            return info.potionImmunities().stream().noneMatch(potionEffect::equals) || super.canBeAffected(effectInstance);
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            if (this.tickCount % 40 == 0) {
                BeeTraitData info = getTraitData();
                if (info.hasTraits() && info.hasParticleEffects()) {
                    info.particleEffects().forEach(basicParticleType -> {
                        for (int i = 0; i < 10; ++i) {
                            this.level.addParticle((ParticleOptions) basicParticleType, this.getRandomX(0.5D),
                                    this.getRandomY() - 0.25D, this.getRandomZ(0.5D),
                                    (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                                    (this.random.nextDouble() - 0.5D) * 2.0D);
                        }
                    });
                }
            }
        } else {
            if (BeeConfig.beesDieInVoid && this.position().y <= level.getMinBuildHeight()) {
                this.remove(RemovalReason.KILLED);
            }
            if (!hasCustomName() && this.tickCount % 100 == 0) {
                if (hasHiveInRange() || hasSavedFlowerPos() || isPassenger() || isPersistenceRequired() || isLeashed() || hasNectar() || disruptorInRange > 0 || isBaby()) {
                    timeWithoutHive = 0;
                } else if ((timeWithoutHive += 100) >= 12000) {
                    this.discard();
                }
                hasHiveInRange = false;
            }
            if (this.tickCount % 100 == 0 && (disruptorInRange--) < 0) {
                disruptorInRange = 0;
            }
        }
        super.aiStep();
    }

    public boolean hasHiveInRange() {
        return hasHiveInRange;
    }

    public void setHasHiveInRange(boolean hasHiveInRange) {
        this.hasHiveInRange = hasHiveInRange;
    }

    public static boolean canBeeSpawn(EntityType<?> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource randomSource) {
        return switch (reason) {
            case NATURAL, CHUNK_GENERATION -> ModSpawnData.test(type, level.getLevel(), pos);
            default -> true;
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FEED_COUNT, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(FEED_COUNT, tag.getInt(NBTConstants.NBT_FEED_COUNT));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NBTConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    @Override
    public AgeableMob createSelectedChild(FamilyUnit family) {
        return (AgeableMob) family.getChildData().entityType().create(level);
    }

    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable Player player) {
        if (player != null && ModUtils.isARealPlayer(player))
            super.setInLove(player);
    }

    @Override
    public void setInLoveTime(int time) {
        //This is overridden bc Botania breeds animals regardless of breeding rules
        // See the method below ( setLove() ) as alternative
        // super.setInLove(time);
    }

    public void setLoveTime(int time) {
        super.setInLoveTime(time);
    }

    @Override
    public void resetLove() {
        super.resetLove();
        resetFeedCount();
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return this.getBreedData().isFood(stack);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isFood(itemstack)) {
            if (!this.level.isClientSide && this.getAge() == 0 && !this.isInLove()) {
                this.usePlayerItem(player, hand, itemstack);
                getBreedData().feedReturnItem().map(ItemStack::copy).ifPresent(player::addItem);
                this.addFeedCount();
                if (this.getFeedCount() >= getBreedData().feedAmount()) {
                    this.setInLove(player);
                }
                player.swing(hand, true);
                return InteractionResult.PASS;
            }

            if (this.isBaby()) {
                this.usePlayerItem(player, hand, itemstack);
                this.ageUp((int) ((-this.getAge() / 20D) * 0.1F), true);
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby()) {
            BlockPos pos = this.blockPosition();
            this.teleportTo(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void setHasDisruptorInRange() {
        disruptorInRange += 2;
        if (disruptorInRange > 10) disruptorInRange = 10;
    }

    public boolean getDisruptorInRange() {
        return disruptorInRange > 0;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, (event) -> {
            event.getController().setAnimation(new AnimationBuilder()
                .addAnimation("animation.bee.fly", ILoopType.EDefaultLoopTypes.LOOP)
                .addAnimation("animation.bee.fly.bobbing", ILoopType.EDefaultLoopTypes.LOOP)
            );
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public ItemStack getHiveOutput(BeehiveTier tier) {
        return getHoneycombData().map(data -> data.getHiveOutput(tier)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getApiaryOutput(ApiaryTier tier) {
        return getHoneycombData().map(data -> data.getApiaryOutput(tier)).orElse(ItemStack.EMPTY);
    }

    @Override
    public int getMaxTimeInHive() {
        return getBeeData().getCoreData().maxTimeInHive();
    }

    @Override
    public void nectarDroppedOff() {
        this.dropOffNectar();
    }

    @Override
    public void setOutOfHiveCooldown(int cooldown) {
        this.setStayOutOfHiveCountdown(cooldown);
    }
    //endregion
}