package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.api.traitdata.BeeAura;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import com.resourcefulbees.resourcefulbees.utils.DamageUtils;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class BeeAuraGoal extends Goal {

    private final CustomBeeEntity entity;

    Random random = new Random();

    public BeeAuraGoal(CustomBeeEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getBeeData().getTraitData().hasBeeAuras();
    }

    @Override
    public void tick() {
        super.tick();
        if (entity.tickCount % 160 == 0) {
            radiateAura();
        }
    }

    private void radiateAura() {
        AxisAlignedBB bb = new AxisAlignedBB(entity.blockPosition()).inflate(entity.getBeeData().getAuraRange());
        List<PlayerEntity> entityList = entity.level.getNearbyPlayers(EntityPredicate.DEFAULT, entity, bb);
        entity.getBeeData().getTraitData().getBeeAuras().forEach(aura -> {
            if (entity.hasEffect(ModEffects.CALMING.get()) && aura.calmingDisabled) return;
            AtomicReference<Boolean> flag = new AtomicReference<>(false);
            AtomicReference<BasicParticleType> type = new AtomicReference<>(ParticleTypes.POOF);
            entityList.forEach(playerEntity -> {
                if (!aura.isBeneficial() && entity.level.getDifficulty() == Difficulty.PEACEFUL) return;
                switch (aura.auraType) {
                    case POTION:
                        if (aura.potionEffect == null) return;
                        grantPotion(playerEntity, aura);
                        type.set(ParticleTypes.WITCH);
                        flag.set(true);
                        break;
                    case BURNING:
                        burnEntity(playerEntity);
                        type.set(ParticleTypes.FLAME);
                        flag.set(true);
                        break;
                    case HEALING:
                        healEntity(playerEntity, aura);
                        type.set(ParticleTypes.HAPPY_VILLAGER);
                        flag.set(true);
                        break;
                    case DAMAGING:
                        damageEntity(playerEntity, aura);
                        type.set(ParticleTypes.CRIT);
                        flag.set(true);
                        break;
                    case EXPERIENCE:
                        giveExperience(playerEntity, aura);
                        type.set(ParticleTypes.ENCHANT);
                        flag.set(true);
                        break;
                    default:
                        // do nothing this aura is broken
                }
                if (Boolean.TRUE.equals(flag.get())) spawnParticles(playerEntity, type.get());
            });
            if (Boolean.TRUE.equals(flag.get())) spawnParticles(entity, type.get());
        });
    }

    private void giveExperience(PlayerEntity playerEntity, BeeAura aura) {
        if (playerEntity.level.isClientSide) return;
        playerEntity.giveExperiencePoints(aura.getExperience());
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;
        serverPlayer.connection.send(new SPlaySoundPacket(SoundEvents.EXPERIENCE_ORB_PICKUP.getLocation(), playerEntity.getSoundSource(), playerEntity.getPosition(0), 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F));
    }

    private void damageEntity(PlayerEntity playerEntity, BeeAura aura) {
        DamageUtils.dealDamage(aura.getDamage(), aura.damageType, playerEntity, entity);
    }

    private void healEntity(PlayerEntity playerEntity, BeeAura aura) {
        playerEntity.heal(aura.getHealing());
    }

    private void burnEntity(PlayerEntity playerEntity) {
        playerEntity.setRemainingFireTicks(200);
    }

    private void grantPotion(PlayerEntity playerEntity, BeeAura aura) {

        playerEntity.addEffect(aura.getInstance(200));
    }

    protected void spawnParticles(LivingEntity livingEntity, IParticleData particleType) {
        float power = 0.1f;
        if (particleType.equals(ParticleTypes.ENCHANT)) {
            power = 1;
        } else if (particleType.equals(ParticleTypes.CRIT)) {
            power = 0.5f;
        }
        if (!entity.level.isClientSide()) {
            ServerWorld worldServer = (ServerWorld) entity.level;
            double d0 = worldServer.random.nextGaussian() * 0.1D;
            double d1 = worldServer.random.nextGaussian() * 0.1D;
            double d2 = worldServer.random.nextGaussian() * 0.1D;
            worldServer.sendParticles(particleType,
                    livingEntity.getX(),
                    livingEntity.getY() + livingEntity.getBbHeight() + 0.1D,
                    livingEntity.getZ(),
                    5, d0, d1, d2, power);
        }
    }
}
