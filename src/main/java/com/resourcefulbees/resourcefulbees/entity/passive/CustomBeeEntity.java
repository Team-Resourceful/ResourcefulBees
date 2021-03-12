package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.SpawnData;
import com.resourcefulbees.resourcefulbees.api.beedata.TraitData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.mixin.AnimalEntityAccessor;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class CustomBeeEntity extends ModBeeEntity implements ICustomBee {

    private static final DataParameter<Integer> FEED_COUNT = EntityDataManager.defineId(CustomBeeEntity.class, DataSerializers.INT);

    protected final CustomBeeData beeData;
    protected int timeWithoutHive;  //<- does not need to be initialized to 0 that is done by default - oreo
    protected int flowerID;
    private BlockPos lastFlower;
    private boolean hasHiveInRange;
    private int disruptorInRange;

    public CustomBeeEntity(EntityType<? extends BeeEntity> type, World world, CustomBeeData beeData) {
        super(type, world);
        this.beeData = beeData;
    }

    public static AttributeModifierMap.MutableAttribute createBeeAttributes(String key) {
        CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(key);
        return createMobAttributes().add(Attributes.MAX_HEALTH, beeData.getCombatData().getBaseHealth()).add(Attributes.FLYING_SPEED, 0.6F).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, beeData.getCombatData().getAttackDamage()).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    //region BEE INFO RELATED METHODS BELOW

    public String getBeeType() {
        return beeData.getName();
    }

    public BlockPos getLastFlower() {
        return lastFlower;
    }

    public void setLastFlower(BlockPos lastFlower) {
        this.lastFlower = lastFlower;
    }

    public CustomBeeData getBeeData() {
        return beeData;
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
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        TraitData info = getBeeData().getTraitData();
        if (hasEffect(Effects.WATER_BREATHING) && source == DamageSource.DROWN) {
            return true;
        }
        if (source.equals(DamageSource.SWEET_BERRY_BUSH)) {
            return true;
        }
        if (info.hasTraits() && info.hasDamageImmunities()) {
            return info.getDamageImmunities().stream().anyMatch(source::equals);
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(@Nonnull EffectInstance effectInstance) {
        TraitData info = getBeeData().getTraitData();
        if (info.hasTraits() && info.hasPotionImmunities()) {
            Effect potionEffect = effectInstance.getEffect();
            return info.getPotionImmunities().stream().noneMatch(potionEffect::equals);
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            if (this.tickCount % 40 == 0) {
                TraitData info = getBeeData().getTraitData();
                if (info.hasTraits() && info.hasParticleEffects()) {
                    info.getParticleEffects().forEach(basicParticleType -> {
                        for (int i = 0; i < 10; ++i) {
                            this.level.addParticle(basicParticleType, this.getRandomX(0.5D),
                                    this.getRandomY() - 0.25D, this.getRandomZ(0.5D),
                                    (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                                    (this.random.nextDouble() - 0.5D) * 2.0D);
                        }
                    });
                }
            }
        } else {
            if (Config.BEES_DIE_IN_VOID.get() && this.position().y <= 0) {
                this.remove();
            }
            if (!hasCustomName() && this.tickCount % 100 == 0) {
                if (hasHiveInRange() || hasSavedFlowerPos() || isPassenger() || isLeashed() || hasNectar() || disruptorInRange > 0) {
                    timeWithoutHive = 0;
                } else {
                    timeWithoutHive += 100;
                    if (timeWithoutHive >= 12000) this.remove();
                }
                hasHiveInRange = false;
            }
            if (this.tickCount % 100 == 0) {
                disruptorInRange--;
                if (disruptorInRange < 0) disruptorInRange = 0;
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

    public static boolean canBeeSpawn(EntityType<? extends AgeableEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        String namespaceID = EntityType.getKey(typeIn).toString();
        String beeType = namespaceID.substring(namespaceID.lastIndexOf(":") + 1, namespaceID.length() - 4);
        SpawnData spawnData = BeeRegistry.getRegistry().getBeeData(beeType).getSpawnData();

        switch (reason) {
            case NATURAL:
            case CHUNK_GENERATION:
                if (spawnData.canSpawnInWorld()) {
                    if (pos.getY() < spawnData.getMinYLevel() || pos.getY() > spawnData.getMaxYLevel()) {
                        return false;
                    }
                    switch (spawnData.getLightLevel()) {
                        case DAY:
                            return worldIn.getMaxLocalRawBrightness(pos) >= 8;
                        case NIGHT:
                            return worldIn.getMaxLocalRawBrightness(pos) <= 7;
                        case ANY:
                            return true;
                    }
                }
                break;
            default:
                return true;
        }

        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FEED_COUNT, 0);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(FEED_COUNT, compound.getInt(NBTConstants.NBT_FEED_COUNT));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putString(NBTConstants.NBT_BEE_TYPE, this.getBeeType());
        compound.putInt(NBTConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    public AgeableEntity createSelectedChild(CustomBeeData customBeeData) {
        EntityType<?> entityType = Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(customBeeData.getEntityTypeRegistryID()));
        Entity entity = entityType.create(level);
        return (AgeableEntity) entity;
    }

    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable PlayerEntity player) {
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
        ((AnimalEntityAccessor) this).setLove(time);
    }

    @Override
    public void resetLove() {
        super.resetLove();
        resetFeedCount();
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack) {
        return BeeInfoUtils.isValidBreedItem(stack, this.getBeeData().getBreedData().getFeedItem());
    }

    @Nonnull
    @Override
    public ActionResultType mobInteract(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem) {
            super.mobInteract(player, hand);
        }
        if (this.isFood(itemstack)) {
            if (!this.level.isClientSide && this.getAge() == 0 && this.canBreed()) {
                this.usePlayerItem(player, itemstack);
                this.addFeedCount();
                if (this.getFeedCount() >= this.getBeeData().getBreedData().getFeedAmount()) {
                    this.setInLove(player);
                }
                player.swing(hand, true);
                return ActionResultType.PASS;
            }

            if (this.isBaby()) {
                this.usePlayerItem(player, itemstack);
                this.ageUp((int) ((-this.getAge() / 20D) * 0.1F), true);
                return ActionResultType.PASS;
            }
        }
        return ActionResultType.FAIL;
    }


    @Nonnull
    @Override
    public EntitySize getDimensions(@Nonnull Pose poseIn) {
        float scale = beeData.getSizeModifier();
        scale = this.isBaby() ? scale * Config.CHILD_SIZE_MODIFIER.get().floatValue() : scale;
        scale = Math.max(scale, 0.75f);
        return super.getDimensions(poseIn).scale(scale);
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
    //endregion
}