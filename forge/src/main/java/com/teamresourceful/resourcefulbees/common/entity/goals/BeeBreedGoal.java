package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

import java.util.Objects;

public class BeeBreedGoal extends BreedGoal {

    private final String beeType;

    public BeeBreedGoal(Animal animal, double speedIn, String beeType) {
        super(animal, speedIn);
        this.beeType = beeType;
    }

    @Override
    public boolean canUse() {
        if (this.animal.isInLove()) {
            if (super.canUse() && this.partner instanceof ICustomBee parent1){
                return BeeRegistry.getRegistry().canParentsBreed(parent1.getBeeType(), ((ICustomBee) animal).getBeeType());
            }
        }
        return false;
    }

    @Override
    protected void breed() {
        if (partner == null) return;
        BeeFamily beeFamily = BeeRegistry.getRegistry().getWeightedChild(((ICustomBee)this.partner).getBeeType(), beeType);

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, this.partner, createSelectedChild(beeFamily));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            resetBreed();
            return;
        }
        AgeableMob selectedChild = event.getChild();
        if (selectedChild != null) {
            awardPlayerAdvancement(selectedChild);
            resetBreed();

            if (beeFamily.chance() >= level.random.nextFloat()) {
                spawnChildInLevel(beeFamily, selectedChild);
            } else {
                this.animal.playSound(SoundEvents.BEE_HURT, 2.0f, 1.0f);
                spawnParticles();
            }
        }
    }

    private void spawnChildInLevel(BeeFamily beeFamily, AgeableMob selectedChild) {
        selectedChild.setAge(beeFamily.getChildData().breedData().childGrowthDelay());
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
        this.animal.setAge(((ICustomBee)this.animal).getBreedData().breedDelay());
        this.partner.setAge(((ICustomBee)this.partner).getBreedData().breedDelay());
        this.animal.resetLove();
        this.partner.resetLove();
    }

    public AgeableMob createSelectedChild(BeeFamily beeFamily) {
        EntityType<?> entityType = Objects.requireNonNull(beeFamily.getChildData().getEntityType());
        return (AgeableMob) entityType.create(level);
    }

    private ServerPlayer getPlayerBreeding() {
        return animal.getLoveCause() == null && partner != null && partner.getLoveCause() != null
                ? this.partner.getLoveCause()
                : animal.getLoveCause();
    }
}
