package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.tileentity.BeehiveTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveTileEntity.Bee.class)
public interface BTEBeeAccessor {

    @Accessor("ticksInHive")
    int getTicksInHive();
}
