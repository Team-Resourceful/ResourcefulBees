package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveBlockEntity.BeeData.class)
public interface BeehiveBeeDataAccessor {

    @Accessor
    int getTicksInHive();

    @Accessor
    CompoundTag getEntityData();

    @Accessor
    int getMinOccupationTicks();
}
