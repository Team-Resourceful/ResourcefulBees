package com.dungeonderps.resourcefulbees.entity.goals;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
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
            return BeeConst.DEFAULT_BEE_TYPE;
    }

    @Override
    protected void spawnBaby() {
        AgeableEntity ageableentity;
        if (getBeeType(this.targetMate).equals(getBeeType(this.animal))) {
            CustomBeeEntity bee = (CustomBeeEntity)this.animal;
            ageableentity = bee.createSelectedChild(getBeeType(this.animal));
        }
        else {
            double selection = MathUtils.nextDouble();
            CustomBeeEntity bee = (CustomBeeEntity)this.animal;
            String parent1 = getBeeType(this.targetMate);
            String parent2 = getBeeType(this.animal);
            int hashcode = BeeInfoUtils.getHashcode(parent1, parent2);
            double weight = bee.getBeeInfo().getBreedWeight();

            if (BeeInfo.FAMILY_TREE.containsKey(hashcode) && selection <= weight){
                String childBee = BeeInfo.FAMILY_TREE.get(hashcode);
                ageableentity = bee.createSelectedChild(childBee);
            } else
                ageableentity = selection <= 0.5
                        ? bee.createSelectedChild(getBeeType(this.animal))
                        : bee.createSelectedChild(getBeeType(this.targetMate));
        }

        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(animal, targetMate, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            this.animal.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
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

            this.animal.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            ageableentity.setGrowingAge(-24000);
            ageableentity.setLocationAndAngles(this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), 0.0F, 0.0F);
            this.world.addEntity(ageableentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), this.animal.getRNG().nextInt(7) + 1));
            }

        }
    }


}
