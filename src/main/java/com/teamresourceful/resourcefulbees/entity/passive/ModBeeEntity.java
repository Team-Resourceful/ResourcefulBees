package com.teamresourceful.resourcefulbees.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ModBeeEntity extends BeeEntity {
    public ModBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean checkIsWithinDistance(@Nonnull BlockPos hivePos, int i) {
        return hivePos.closerThan(this.blockPosition(), i);
    }
}
