package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.mixin.BeeEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class ModBeeEntity extends BeeEntity {
    public ModBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean checkIsWithinDistance(@Nonnull BlockPos hivePos, int i) {
        return hivePos.withinDistance(this.getBlockPos(), i);
    }
}
