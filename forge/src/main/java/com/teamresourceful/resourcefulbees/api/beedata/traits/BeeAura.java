package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record BeeAura(AuraType type, DamageEffect damageEffect, PotionEffect potionEffect, int modifier, boolean calmingDisabled) {

    public static final int INTERVAL = CommonConfig.AURA_FREQUENCY.get() * 20;

    public static final Codec<BeeAura> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AuraType.CODEC.fieldOf("aura").forGetter(BeeAura::type),
            DamageEffect.CODEC.fieldOf("damageEffect").orElse(DamageEffect.DEFAULT).forGetter(BeeAura::damageEffect),
            PotionEffect.CODEC.fieldOf("potionEffect").orElse(PotionEffect.DEFAULT).forGetter(BeeAura::potionEffect),
            Codec.INT.fieldOf("modifier").orElse(0).forGetter(BeeAura::modifier),
            Codec.BOOL.fieldOf("calmingDisabled").orElse(false).forGetter(BeeAura::calmingDisabled)
    ).apply(instance, BeeAura::new));

    public boolean isBeneficial() {
        return type.isPotion() ? potionEffect.effect().isBeneficial() : type.isBeneficial();
    }

    public void apply(ServerPlayer player) {
        switch (type) {
            case POTION -> player.addEffect(potionEffect.createInstance(BeeAura.INTERVAL));
            case BURNING -> player.setRemainingFireTicks(BeeAura.INTERVAL);
            case HEALING -> player.heal(modifier);
            case DAMAGING -> player.hurt(damageEffect.getDamageSource(player), damageEffect.strength());
            case EXPERIENCE, EXPERIENCE_DRAIN -> {
                player.giveExperiencePoints(modifier);
                SoundEvent sound = type == AuraType.EXPERIENCE ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.GRINDSTONE_USE;
                playSound(sound, player);
            }
        }
        spawnParticles(player, type.particle);
    }

    private static void playSound(SoundEvent sound, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playSound(sound, 0.1f, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.35F + 0.9F);
        }
    }

    public static void spawnParticles(LivingEntity entity, SimpleParticleType particle) {
        float power = particle.equals(ParticleTypes.ENCHANT) ? 1f : particle.equals(ParticleTypes.CRIT) ? 0.5f : 0.1f;
        if (entity.getLevel() instanceof ServerLevel level) {
            double d0 = level.random.nextGaussian() * 0.1D;
            double d1 = level.random.nextGaussian() * 0.1D;
            double d2 = level.random.nextGaussian() * 0.1D;
            level.sendParticles(particle,
                    entity.getX(), entity.getY() + entity.getBbHeight() + 0.1D, entity.getZ(),
                    5, d0, d1, d2, power);
        }
    }
}
