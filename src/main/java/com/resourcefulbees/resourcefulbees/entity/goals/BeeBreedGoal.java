package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeWorldServer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class BeeBreedGoal extends BreedGoal {

    public BeeBreedGoal(AnimalEntity animal, double speedIn) {
        super(animal, speedIn);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.field_75391_e = this.getNearbyMate();
            if (field_75391_e instanceof ICustomBee){
                ICustomBee parent1 = ((ICustomBee) field_75391_e);
                ICustomBee parent2 = ((ICustomBee) animal);
                return BeeRegistry.getRegistry().canParentsBreed(parent1.getBeeType(), parent2.getBeeType());
            }
            else
                return false;
        }
    }

    @Override
    protected void spawnBaby() {
        ICustomBee bee = (ICustomBee)this.animal;
        String parent1 = ((ICustomBee)this.field_75391_e).getBeeType();
        String parent2 = ((ICustomBee)this.animal).getBeeType();
        CustomBeeData childData = BeeRegistry.getRegistry().getWeightedChild(parent1, parent2);
        float breedChance = BeeRegistry.getRegistry().getBreedChance(parent1, parent2, childData);
        AgeableEntity ageableentity = bee.createSelectedChild(childData);

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, field_75391_e, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            int p1_breedDelay = ((ICustomBee)this.animal).getBeeData().getBreedData().getBreedDelay();
            int p2_breedDelay = ((ICustomBee)this.field_75391_e).getBeeData().getBreedData().getBreedDelay();

            resetBreed(p1_breedDelay, p2_breedDelay);
            return;
        }
        if (ageableentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.field_75391_e.getLoveCause() != null) {
                serverplayerentity = this.field_75391_e.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.field_75391_e, ageableentity);
            }
            int p1_breedDelay = ((ICustomBee)this.animal).getBeeData().getBreedData().getBreedDelay();
            int p2_breedDelay = ((ICustomBee)this.field_75391_e).getBeeData().getBreedData().getBreedDelay();
            resetBreed(p1_breedDelay, p2_breedDelay);


            float nextFloat = world.rand.nextFloat();
            if (breedChance >= nextFloat) {
                ageableentity.setGrowingAge(childData.getBreedData().getChildGrowthDelay());
                ageableentity.setLocationAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
                this.world.addEntity(ageableentity);
                this.world.setEntityState(this.animal, (byte)18);
                if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                    this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRNG().nextInt(7) + 1));
                }
            }else {
                this.animal.playSound(SoundEvents.ENTITY_BEE_HURT, 2.0f, 1.0f);
                spawnParticles(ParticleTypes.ANGRY_VILLAGER);
            }
        }
    }

    protected void spawnParticles(IParticleData p_213718_1_) {
        if (!world.isRemote()) {
            ServerWorld worldServer = (ServerWorld)world;
            for(int i = 0; i < 5; ++i) {
                double d0 = world.rand.nextGaussian() * 0.02D;
                double d1 = world.rand.nextGaussian() * 0.02D;
                double d2 = world.rand.nextGaussian() * 0.02D;
                worldServer.spawnParticle(p_213718_1_,
                        this.animal.getParticleX(1.0D),
                        this.animal.getRandomBodyY(),
                        this.animal.getParticleZ(1.0D),
                        50, d0, d1, d2, 0.1f);
            }
        }
    }

    private void resetBreed(int p1_breedDelay, int p2BreedDelay) {
        this.animal.setGrowingAge(p1_breedDelay);
        this.field_75391_e.setGrowingAge(p2BreedDelay);
        this.animal.resetInLove();
        this.field_75391_e.resetInLove();
    }
}
