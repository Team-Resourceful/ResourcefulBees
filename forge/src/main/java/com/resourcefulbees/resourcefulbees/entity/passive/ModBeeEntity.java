package com.resourcefulbees.resourcefulbees.entity.passive;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ModBeeEntity extends Bee {
    public ModBeeEntity(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    public boolean checkIsWithinDistance(@Nonnull BlockPos hivePos, int i) {
        return hivePos.closerThan(this.blockPosition(), i);
    }
}
