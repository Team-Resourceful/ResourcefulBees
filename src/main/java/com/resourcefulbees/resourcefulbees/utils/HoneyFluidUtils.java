package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.api.IEntity;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.TagConstants;
import com.resourcefulbees.resourcefulbees.mixin.EntityAccessor;
import com.resourcefulbees.resourcefulbees.mixin.LivingEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

/**
 * @author TelepathicGrunt
 * <p>Copied from Bumblezone.</p>
 */
public class HoneyFluidUtils {

    private HoneyFluidUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void breathing(LivingEntity entity) {
        boolean invulnerable = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.invulnerable;
        boolean isBee = entity instanceof BeeEntity;
        if (isBee) return;
        if (entity.isAlive()) {
            if (entity.isEyeInFluid(TagConstants.RESOURCEFUL_HONEY)) {
                if (!entity.canBreatheUnderwater() && !EffectUtils.hasWaterBreathing(entity) && !invulnerable) {
                    entity.setAirSupply(
                            decreaseAirSupply(
                                    entity.getAirSupply() - 4, // -4 to counteract the +4 for rebreathing as vanilla thinks the honey fluid is air
                                    entity,
                                    entity.level.random)
                    );
                    if (entity.getAirSupply() == -20) {
                        entity.setAirSupply(0);
                        Vector3d vector3d = entity.getDeltaMovement();

                        for (int i = 0; i < 8; ++i) {
                            double d2 = entity.level.random.nextDouble() - entity.level.random.nextDouble();
                            double d3 = entity.level.random.nextDouble() - entity.level.random.nextDouble();
                            double d4 = entity.level.random.nextDouble() - entity.level.random.nextDouble();
                            entity.level.addParticle(ParticleTypes.BUBBLE, entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4, vector3d.x, vector3d.y, vector3d.z);
                        }

                        entity.hurt(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!entity.level.isClientSide() && entity.isPassenger() && entity.getVehicle() != null && !entity.getVehicle().canBeRiddenInWater(entity)) {
                    entity.stopRiding();
                }
            }
        }
    }

    protected static int decreaseAirSupply(int airSupply, LivingEntity entity, Random random) {
        int respiration = EnchantmentHelper.getRespiration(entity);
        return respiration > 0 && random.nextInt(respiration + 1) > 0 ? airSupply : airSupply - 1;
    }

    public static void doJump(boolean touchedHoney, LivingEntity entity) {
        double fluidHeight = entity.getFluidHeight(FluidTags.WATER);
        boolean flag = touchedHoney && fluidHeight > 0.0D;
        double jumpThreshold = entity.getFluidJumpThreshold();
        if (flag || !entity.isOnGround() && !(fluidHeight > jumpThreshold)) {
            ((LivingEntityAccessor) entity).callJumpInLiquid(TagConstants.RESOURCEFUL_HONEY);
        }
    }

    public static void doFluidMovement(Entity entity) {

        EntityAccessor accessor = ((EntityAccessor) entity);
        IEntity implEntity = ((IEntity) entity);

        if (entity.getVehicle() instanceof BoatEntity) {
            implEntity.setTouchingHoney(false);
        }
        if (entity.updateFluidHeightAndDoFluidPushing(TagConstants.RESOURCEFUL_HONEY, 0.014D)) {
            if (!((IEntity) entity).getTouchedHoney() && !accessor.getFirstTick()) {
                makeSplash(entity);
            }
            entity.fallDistance = 0.0F;
            ((IEntity) entity).setTouchingHoney(true);
            entity.clearFire();
        } else {
            ((IEntity) entity).setTouchingHoney(false);
        }
    }

    private static void makeSplash(Entity entity) {
        EntityAccessor accessor = ((EntityAccessor) entity);
        Entity boat = entity.isVehicle() && entity.getControllingPassenger() != null ? entity.getControllingPassenger() : entity;
        float f = boat == entity ? 0.2F : 0.9F;
        Vector3d vector3d = entity.getDeltaMovement();
        float f1 = MathHelper.sqrt(vector3d.x * vector3d.x * (double) 0.2F + vector3d.y * vector3d.y + vector3d.z * vector3d.z * (double) 0.2F) * f;
        if (f1 > 1.0F) {
            f1 = 1.0F;
        }
        entity.playSound(SoundEvents.HONEY_BLOCK_FALL, f1 * 2, 1.0F + (accessor.getRandom().nextFloat() - accessor.getRandom().nextFloat()) * 0.4F);
    }
}
