package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeAura;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BeeAuraGoal extends Goal {

    private final CustomBeeEntity bee;

    public BeeAuraGoal(CustomBeeEntity bee) {
        this.bee = bee;
    }

    @Override
    public boolean canUse() {
        return this.bee.getTraitData().hasAuras();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.bee.tickCount % 160 == 0) {
            AABB box = new AABB(this.bee.blockPosition()).inflate(this.bee.getTraitData().getAuraRange());
            List<Player> players = this.bee.getLevel().getNearbyPlayers(TargetingConditions.DEFAULT, this.bee, box);

            for (BeeAura aura : this.bee.getTraitData().getAuras()) {
                if (this.bee.hasEffect(ModEffects.CALMING.get()) && aura.calmingDisabled()) continue;
                if (!aura.isBeneficial() && this.bee.level.getDifficulty() == Difficulty.PEACEFUL) continue;
                for (Player player : players) {
                    switch (aura.type()) {
                        case POTION -> player.addEffect(aura.potionEffect().createInstance(200));
                        case BURNING -> player.setRemainingFireTicks(200);
                        case HEALING -> player.heal(aura.modifier());
                        case DAMAGING -> player.hurt(aura.damageEffect().getDamageSource(player), aura.damageEffect().strength());
                        case EXPERIENCE, EXPERIENCE_DRAIN -> {
                            player.giveExperiencePoints(aura.modifier());
                            SoundEvent sound = aura.type() == AuraType.EXPERIENCE ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.GRINDSTONE_USE;
                            playSound(sound, player);
                        }
                    }
                    spawnParticles(player, aura.type().particle);
                }
                spawnParticles(this.bee, aura.type().particle);
            }
        }
    }

    private static void playSound(SoundEvent sound, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playSound(sound, 0.1f, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.35F + 0.9F);
        }
    }

    protected void spawnParticles(LivingEntity livingEntity, SimpleParticleType particle) {
        float power = particle.equals(ParticleTypes.ENCHANT) ? 1f : particle.equals(ParticleTypes.CRIT) ? 0.5f : 0.1f;
        if (this.bee.getLevel() instanceof ServerLevel level) {
            double d0 = level.random.nextGaussian() * 0.1D;
            double d1 = level.random.nextGaussian() * 0.1D;
            double d2 = level.random.nextGaussian() * 0.1D;
            level.sendParticles(particle,
                    livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() + 0.1D, livingEntity.getZ(),
                    5, d0, d1, d2, power);
        }
    }
}
