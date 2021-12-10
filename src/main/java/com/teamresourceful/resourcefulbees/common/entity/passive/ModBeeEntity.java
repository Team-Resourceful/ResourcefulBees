package com.teamresourceful.resourcefulbees.common.entity.passive;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ModBeeEntity extends Bee {
    public ModBeeEntity(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    public boolean checkIsWithinDistance(@NotNull BlockPos hivePos, int i) {
        return hivePos.closerThan(this.blockPosition(), i);
    }
}
