package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BeehiveTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveTileEntity.Bee.class)
public interface BTEBeeAccessor {

    @Accessor
    int getTicksInHive();

    @Accessor
    CompoundNBT getEntityData();
}
