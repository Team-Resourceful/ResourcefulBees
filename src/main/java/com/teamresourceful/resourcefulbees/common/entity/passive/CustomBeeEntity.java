package com.teamresourceful.resourcefulbees.common.entity.passive;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.api.IBeeCompat;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.CombatData;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.api.spawndata.SpawnData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.dynamic.SpawnerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;
import java.util.Optional;

public class CustomBeeEntity extends Bee implements ICustomBee, IAnimatable, IBeeCompat {

    private static final EntityDataAccessor<Integer> FEED_COUNT = SynchedEntityData.defineId(CustomBeeEntity.class, EntityDataSerializers.INT);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bee.fly", true).addAnimation("animation.bee.fly.bobbing", true));
        return PlayState.CONTINUE;
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    protected final CustomBeeData customBeeData;
    protected final String beeType;
    protected int timeWithoutHive;
    protected int flowerID;
    private boolean hasHiveInRange;
    private int disruptorInRange;

    public CustomBeeEntity(EntityType<? extends Bee> type, Level world, String beeType) {
        super(type, world);
        this.beeType = beeType;
        this.customBeeData = BeeRegistry.getRegistry().getBeeData(beeType);
    }

    public static AttributeSupplier.Builder createBeeAttributes(String key) {
        CombatData combatData = BeeRegistry.getRegistry().getBeeData(key).combatData();
        AttributeSupplier.Builder builder = createMobAttributes();
        CombatData.DEFAULT_ATTRIBUTES.forEach(builder::add);
        combatData.attributes().forEach(builder::add);
        return builder;
    }

    //region BEE INFO RELATED METHODS BELOW

    public String getBeeType() {
        return beeType;
    }

    @Nullable
    public JsonObject getRawBeeData() {
        return customBeeData.rawData();
    }

    public CoreData getCoreData() {
        return customBeeData.coreData();
    }

    public Optional<OutputVariation> getHoneycombData() {
        return getCoreData().getHoneycombData();
    }

    public RenderData getRenderData() {
        return customBeeData.renderData();
    }

    public BreedData getBreedData() {
        return customBeeData.breedData();
    }

    public CombatData getCombatData() {
        return customBeeData.combatData();
    }

    public MutationData getMutationData() {
        return customBeeData.mutationData();
    }

    public TraitData getTraitData() {
        return customBeeData.traitData();
    }

    public int getFlowerEntityID() {
        return flowerID;
    }

    public void setFlowerEntityID(int id) {
        flowerID = id;
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

        TraitData info = getTraitData();
        if (hasEffect(MobEffects.WATER_BREATHING) && source == DamageSource.DROWN) {
            return true;
        }
        if (source.equals(DamageSource.SWEET_BERRY_BUSH)) {
            return true;
        }
        if (info.hasTraits() && info.hasDamageImmunities() && info.getDamageImmunities().stream().anyMatch(source.msgId::equalsIgnoreCase)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effectInstance) {
        TraitData info = getTraitData();
        if (info.hasTraits() && info.hasPotionImmunities()) {
            MobEffect potionEffect = effectInstance.getEffect();
            return info.getPotionImmunities().stream().noneMatch(potionEffect::equals) || super.canBeAffected(effectInstance);
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            if (this.tickCount % 40 == 0) {
                TraitData info = getTraitData();
                if (info.hasTraits() && info.hasParticleEffects()) {
                    info.getParticleEffects().forEach(basicParticleType -> {
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
            if (Boolean.TRUE.equals(CommonConfig.BEES_DIE_IN_VOID.get()) && this.position().y <= level.getMinBuildHeight()) {
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

    public static boolean canBeeSpawn(String beeType, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos) {
        switch (reason) {
            case NATURAL, CHUNK_GENERATION -> {
                SpawnData data = SpawnerRegistry.getData(beeType);
                return data.yLevel().isValueInRange(pos.getY()) && data.lightLevel().canSpawn(worldIn, pos);
            }
        }
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FEED_COUNT, 0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(FEED_COUNT, compound.getInt(NBTConstants.NBT_FEED_COUNT));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NBTConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    public AgeableMob createSelectedChild(BeeFamily beeFamily) {
        EntityType<?> entityType = Objects.requireNonNull(beeFamily.getChildData().getEntityType());
        return (AgeableMob) entityType.create(level);
    }

    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable Player player) {
        if (player != null && !(player instanceof FakePlayer))
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
        return this.getBreedData().feedItems().contains(stack.getItem().builtInRegistryHolder());
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem) {
            super.mobInteract(player, hand);
        }
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
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public CustomBeeData getBeeData() {
        return customBeeData;
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
        return getBeeData().coreData().maxTimeInHive();
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