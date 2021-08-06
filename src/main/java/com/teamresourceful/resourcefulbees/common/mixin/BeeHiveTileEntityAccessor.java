package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.tileentity.BeehiveTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BeehiveTileEntity.class)
public interface BeeHiveTileEntityAccessor {

    @Accessor("stored")
    List<BeehiveTileEntity.Bee> getBees();
}
