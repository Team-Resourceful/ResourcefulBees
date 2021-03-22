package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class BeeBreedGoal extends BreedGoal {

    public BeeBreedGoal(AnimalEntity animal, double speedIn) {
        super(animal, speedIn);
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            if (partner instanceof ICustomBee){
                ICustomBee parent1 = ((ICustomBee) partner);
                ICustomBee parent2 = ((ICustomBee) animal);
                return BeeRegistry.getRegistry().canParentsBreed(parent1.getBeeType(), parent2.getBeeType());
            }
            else
                return false;
        }
    }

    @Override
    protected void breed() {
        ICustomBee bee = (ICustomBee)this.animal;
        String parent1 = ((ICustomBee)this.partner).getBeeType();
        String parent2 = ((ICustomBee)this.animal).getBeeType();
        CustomBeeData childData = BeeRegistry.getRegistry().getWeightedChild(parent1, parent2);
        float breedChance = BeeRegistry.getRegistry().getBreedChance(parent1, parent2, childData);
        AgeableEntity ageableentity = bee.createSelectedChild(childData);

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, partner, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            int p1BreedDelay = ((ICustomBee)this.animal).getBeeData().getBreedData().getBreedDelay();
            int p2BreedDelay = ((ICustomBee)this.partner).getBeeData().getBreedData().getBreedDelay();

            resetBreed(p1BreedDelay, p2BreedDelay);
            return;
        }
        if (ageableentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.partner.getLoveCause() != null) {
                serverplayerentity = this.partner.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, ageableentity);
            }
            int p1BreedDelay = ((ICustomBee)this.animal).getBeeData().getBreedData().getBreedDelay();
            int p2BreedDelay = ((ICustomBee)this.partner).getBeeData().getBreedData().getBreedDelay();
            resetBreed(p1BreedDelay, p2BreedDelay);


            float nextFloat = level.random.nextFloat();
            if (breedChance >= nextFloat) {
                ageableentity.setAge(childData.getBreedData().getChildGrowthDelay());
                ageableentity.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
                this.level.addFreshEntity(ageableentity);
                this.level.broadcastEntityEvent(this.animal, (byte)18);
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
                }
            }else {
                this.animal.playSound(SoundEvents.BEE_HURT, 2.0f, 1.0f);
                spawnParticles();
            }
        }
    }

    protected void spawnParticles() {
        if (!level.isClientSide()) {
            ServerWorld worldServer = (ServerWorld)level;
            for(int i = 0; i < 5; ++i) {
                double d0 = level.random.nextGaussian() * 0.02D;
                double d1 = level.random.nextGaussian() * 0.02D;
                double d2 = level.random.nextGaussian() * 0.02D;
                worldServer.sendParticles((IParticleData) ParticleTypes.ANGRY_VILLAGER,
                        this.animal.getRandomX(1.0D),
                        this.animal.getRandomY(),
                        this.animal.getRandomZ(1.0D),
                        50, d0, d1, d2, 0.1f);
            }
        }
    }

    private void resetBreed(int p1BreedDelay, int p2BreedDelay) {
        this.animal.setAge(p1BreedDelay);
        this.partner.setAge(p2BreedDelay);
        this.animal.resetLove();
        this.partner.resetLove();
    }
}
