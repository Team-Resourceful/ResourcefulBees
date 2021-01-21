package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.SpawnData;
import com.resourcefulbees.resourcefulbees.api.beedata.TraitData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class CustomBeeEntity extends ModBeeEntity implements ICustomBee {

    private static final DataParameter<Integer> FEED_COUNT = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.VARINT);

    protected final CustomBeeData beeData;
    protected int timeWithoutHive;  //<- does not need to be initialized to 0 that is done by default - oreo
    protected int flowerID;
    public BlockPos lastFlower;
    private boolean hasHiveInRange;
    private int disrupterInRange;

    public CustomBeeEntity(EntityType<? extends BeeEntity> type, World world, CustomBeeData beeData) {
        super(type, world);
        this.beeData = beeData;
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
        return this.dataManager.get(FEED_COUNT);
    }

    @Override
    public void resetFeedCount() {
        this.dataManager.set(FEED_COUNT, 0);
    }

    @Override
    public void addFeedCount() {
        this.dataManager.set(FEED_COUNT, this.getFeedCount() + 1);
    }
    //endregion

    //region CUSTOM BEE RELATED METHODS BELOW

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        TraitData info = getBeeData().getTraitData();
        if (source.equals(DamageSource.SWEET_BERRY_BUSH)) {
            return true;
        }
        if (info.hasDamageImmunities()) {
            for (DamageSource damage : info.getDamageImmunities())
                if (source.equals(damage)) return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPotionApplicable(@Nonnull EffectInstance potioneffectIn) {
        TraitData info = getBeeData().getTraitData();
        if (info.hasPotionImmunities()) {
            for (Effect potion : info.getPotionImmunities()) {
                if (potion.equals(potioneffectIn.getPotion())) return false;
            }
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void livingTick() {
        if (this.world.isRemote) {
            if (this.ticksExisted % 40 == 0) {
                TraitData info = getBeeData().getTraitData();
                if (info.hasParticleEffects()) {
                    for (BasicParticleType particle : info.getParticleEffects()) {
                        for (int i = 0; i < 10; ++i) {
                            this.world.addParticle(particle, this.getParticleX(0.5D),
                                    this.getRandomBodyY() - 0.25D, this.getParticleZ(0.5D),
                                    (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
                                    (this.rand.nextDouble() - 0.5D) * 2.0D);
                        }
                    }
                }
            }
        } else {
            if (Config.BEES_DIE_IN_VOID.get() && this.getPositionVec().y <= 0) {
                this.remove();
            }
            if (!hasCustomName()) {
                if (this.ticksExisted % 100 == 0) {
                    if (hasHiveInRange() || hasFlower() || isPassenger() || getLeashed() || hasNectar()) {
                        timeWithoutHive = 0;
                    } else {
                        timeWithoutHive += 100;
                        if (timeWithoutHive >= 12000) this.remove();
                    }
                    hasHiveInRange = false;
                }
            }
            if (this.ticksExisted % 100 == 0) {
                disrupterInRange--;
                if (disrupterInRange < 0) disrupterInRange = 0;
            }
        }
        super.livingTick();
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
            case BREEDING:
                if (spawnData.canSpawnInWorld()) {
                    switch (spawnData.getLightLevel()) {
                        case DAY:
                            return worldIn.getLight(pos) >= 8;
                        case NIGHT:
                            return worldIn.getLight(pos) <= 7;
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
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FEED_COUNT, 0);
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(FEED_COUNT, compound.getInt(NBTConstants.NBT_FEED_COUNT));
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(NBTConstants.NBT_BEE_TYPE, this.getBeeType());
        compound.putInt(NBTConstants.NBT_FEED_COUNT, this.getFeedCount());
    }

    public AgeableEntity createSelectedChild(CustomBeeData customBeeData) {
        EntityType<?> entityType = Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(customBeeData.getEntityTypeRegistryID()));
        Entity entity = entityType.create(world);
        return (AgeableEntity) entity;
    }

    //This is because we don't want IF being able to breed our animals
    @Override
    public void setInLove(@Nullable PlayerEntity player) {
        if (player != null && !(player instanceof FakePlayer))
            super.setInLove(player);
    }

    @Override
    public void resetInLove() {
        super.resetInLove();
        resetFeedCount();
    }

    @Override
    public boolean isBreedingItem(@Nonnull ItemStack stack) {
        return BeeInfoUtils.isValidBreedItem(stack, this.getBeeData().getBreedData().getFeedItem());
    }

    @Nonnull
    @Override
    public ActionResultType interactMob(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem) {
            super.interactMob(player, hand);
        }
        if (this.isBreedingItem(itemstack)) {
            if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
                this.consumeItemFromStack(player, itemstack);
                this.addFeedCount();
                if (this.getFeedCount() >= this.getBeeData().getBreedData().getFeedAmount()) {
                    this.setInLove(player);
                }
                player.swingHand(hand, true);
                return ActionResultType.PASS;
            }

            if (this.isChild()) {
                this.consumeItemFromStack(player, itemstack);
                this.ageUp((int) ((-this.getGrowingAge() / 20) * 0.1F), true);
                return ActionResultType.PASS;
            }
        }
        return ActionResultType.FAIL;
    }


    @Nonnull
    @Override
    public EntitySize getSize(@Nonnull Pose poseIn) {
        float scale = beeData.getSizeModifier();
        scale = this.isChild() ? scale * Config.CHILD_SIZE_MODIFIER.get().floatValue() : scale;
        return super.getSize(poseIn).scale(scale);
    }

    @Override
    public float getEyeHeight(@NotNull Pose p_213307_1_) {
        return super.getEyeHeight(p_213307_1_);
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> parameter) {
        super.notifyDataManagerChange(parameter);
    }

    @Override
    protected void onGrowingAdult() {
        super.onGrowingAdult();
        if (!this.isChild()) {
            BlockPos pos = this.getBlockPos();
            this.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void setHasDistrupterInRange() {
        disrupterInRange += 2;
        if (disrupterInRange > 10) disrupterInRange = 10;
    }

    public boolean isDisrupterInRange() {
        return disrupterInRange > 0;
    }
    //endregion
}