package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
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
            this.field_75391_e = this.getNearbyMate();
            if (field_75391_e instanceof CustomBeeEntity){
                CustomBeeEntity parent1 = ((CustomBeeEntity) field_75391_e);
                CustomBeeEntity parent2 = ((CustomBeeEntity) animal);
                return BeeRegistry.canParentsBreed(parent1.getBeeType(), parent2.getBeeType());
            }
            else
                return false;
        }
    }

    @Override
    protected void spawnBaby() {
        AgeableEntity ageableentity;
        CustomBeeEntity bee = (CustomBeeEntity)this.animal;
        String parent1 = getBeeType(this.field_75391_e);
        String parent2 = getBeeType(this.animal);
        ageableentity = bee.createSelectedChild(BeeRegistry.getWeightedChild(parent1, parent2));

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, field_75391_e, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            resetBreed();
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

            resetBreed();
            ageableentity.setGrowingAge(-24000);
            ageableentity.setLocationAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            this.world.addEntity(ageableentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRNG().nextInt(7) + 1));
            }

        }
    }

    private void resetBreed() {
        this.animal.setGrowingAge(6000);
        this.field_75391_e.setGrowingAge(6000);
        this.animal.resetInLove();
        this.field_75391_e.resetInLove();
    }
}
