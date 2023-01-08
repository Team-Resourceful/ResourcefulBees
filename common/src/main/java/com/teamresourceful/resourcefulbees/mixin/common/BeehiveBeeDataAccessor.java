package com.teamresourceful.resourcefulbees.mixin.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveBlockEntity.BeeData.class)
public interface BeehiveBeeDataAccessor {

    @Accessor
    int getTicksInHive();

    @Accessor
    void setTicksInHive(int ticksInHive);

    @Accessor
    CompoundTag getEntityData();

    @Accessor
    int getMinOccupationTicks();
}
