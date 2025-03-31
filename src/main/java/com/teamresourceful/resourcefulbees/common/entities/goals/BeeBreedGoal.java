package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.GameRules;

public class BeeBreedGoal extends BreedGoal {

    private final String beeType;

    public <T extends Bee & CustomBee> BeeBreedGoal(T animal, double speedIn, String beeType) {
        super(animal, speedIn);
        this.beeType = beeType;
    }

    @Override
    public boolean canUse() {
        if (this.animal.isInLove() && super.canUse() && this.partner instanceof CustomBee parent1){
            return BeeRegistry.get().canParentsBreed(parent1.getBeeType(), beeType);
        }
        return false;
    }

    @Override
    protected void breed() {
        if (partner == null) return;
        FamilyUnit beeFamily = BeeRegistry.get().getWeightedChild(((CustomBee)this.partner).getBeeType(), beeType);

        final SpawnBabyEvent event = new SpawnBabyEvent(animal, this.partner, ((CustomBee)this.animal).createSelectedChild(beeFamily));
        ModUtils.spawnBabyEvent(event);
        if (event.isCanceled()) {
            resetBreed();
            return;
        }
        AgeableMob selectedChild = event.getChild();
        if (selectedChild != null) {
            awardPlayerAdvancement(selectedChild);
            resetBreed();

            if (beeFamily.chance() >= level.random.nextFloat()) {
                selectedChild.setPersistenceRequired();
                spawnChildInLevel(beeFamily, selectedChild);
            } else {
                this.animal.playSound(SoundEvents.BEE_HURT, 2.0f, 1.0f);
                spawnParticles();
            }
        }
    }

    private void spawnChildInLevel(FamilyUnit beeFamily, AgeableMob selectedChild) {
        selectedChild.setAge(beeFamily.getChildData().getBreedData().childGrowthDelay());
        selectedChild.moveTo(animal.position());
        this.level.addFreshEntity(selectedChild);
        this.level.broadcastEntityEvent(this.animal, (byte)18);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
        }
    }

    private void awardPlayerAdvancement(AgeableMob selectedChild) {
        ServerPlayer serverPlayer = getPlayerBreeding();
        if (serverPlayer != null && partner != null) {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this.animal, this.partner, selectedChild);
        }
    }

    protected void spawnParticles() {
        if (level instanceof ServerLevel serverLevel) {
            for(int i = 0; i < 5; ++i) {
                double d0 = level.random.nextGaussian() * 0.02D;
                double d1 = level.random.nextGaussian() * 0.02D;
                double d2 = level.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                        this.animal.getRandomX(1.0D),
                        this.animal.getRandomY(),
                        this.animal.getRandomZ(1.0D),
                        50, d0, d1, d2, 0.1f);
            }
        }
    }

    private void resetBreed() {
        this.animal.setAge(((CustomBee)this.animal).getBreedData().breedDelay());
        this.partner.setAge(((CustomBee)this.partner).getBreedData().breedDelay());
        this.animal.resetLove();
        this.partner.resetLove();
    }

    private ServerPlayer getPlayerBreeding() {
        return animal.getLoveCause() == null && partner != null && partner.getLoveCause() != null
                ? this.partner.getLoveCause()
                : animal.getLoveCause();
    }
}
