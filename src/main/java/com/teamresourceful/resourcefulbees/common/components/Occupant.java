package com.teamresourceful.resourcefulbees.common.components;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface Occupant {

    @Nullable Entity createEntity(Level level, BlockPos hivePos);

    default void setBeeReleaseData(int ticksInContainer, Bee bee) {
        updateBeeAge(ticksInContainer, bee);
        bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - ticksInContainer));
        if (bee instanceof CustomBeeEntity customBee) customBee.setLoveTime(Math.max(0, bee.getInLoveTime() - ticksInContainer));
    }

    default void updateBeeAge(int ticksInContainer, Bee bee) {
        if (!bee.isAgeLocked()) {
            int age = bee.getAge();
            if (age < 0) {
                bee.setAge(Math.min(0, age + ticksInContainer));
            } else if (age > 0) {
                bee.setAge(Math.max(0, age - ticksInContainer));
            }
        }
    }
}
