package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;

public class BeeBreedGoal extends BreedGoal {
    public BeeBreedGoal(AnimalEntity animal, double speedIn) {
        super(animal, speedIn);
    }

    public String getBeeType(AnimalEntity entity) {
        Entity beeEntity = entity.getEntity();
        if (beeEntity instanceof CustomBeeEntity) {
            CustomBeeEntity bee = (CustomBeeEntity)beeEntity;
            return bee.getBeeType();
        }
        else
            return BeeConstants.DEFAULT_BEE_TYPE;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.targetMate = this.getNearbyMate();
            if (targetMate instanceof CustomBeeEntity){
                CustomBeeEntity parent1 = ((CustomBeeEntity) targetMate);
                CustomBeeEntity parent2 = ((CustomBeeEntity) animal);
                if (BeeInfo.canParentsBreed(parent1.getBeeType(), parent2.getBeeType()))
                    return true;
                else return parent1.getBeeType().equals(parent2.getBeeType());
            }
            else
                return false;
        }
    }

    @Override
    protected void spawnBaby() {
        AgeableEntity ageableentity;
        if (getBeeType(this.targetMate).equals(getBeeType(this.animal))) {
            CustomBeeEntity bee = (CustomBeeEntity)this.animal;
            ageableentity = bee.createSelectedChild(getBeeType(this.animal));
        }
        else {
            CustomBeeEntity bee = (CustomBeeEntity)this.animal;
            String parent1 = getBeeType(this.targetMate);
            String parent2 = getBeeType(this.animal);
            ageableentity = bee.createSelectedChild(BeeInfo.getWeightedChild(parent1, parent2));
        }

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, targetMate, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            resetBreed();
            return;
        }
        if (ageableentity != null) {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
                serverplayerentity = this.targetMate.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, ageableentity);
            }

            resetBreed();
            ageableentity.setGrowingAge(-24000);
            ageableentity.setLocationAndAngles(this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), 0.0F, 0.0F);
            this.world.addEntity(ageableentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), this.animal.getRNG().nextInt(7) + 1));
            }

        }
    }

    private void resetBreed() {
        this.animal.setGrowingAge(6000);
        this.targetMate.setGrowingAge(6000);
        this.animal.resetInLove();
        this.targetMate.resetInLove();
    }
}
