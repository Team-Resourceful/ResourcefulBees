package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeehiveTileEntity.class)
public interface BeehiveTileEntityInvoker {

    @Invoker("ageBee")
    void invokeAgeBee(int ticksInHive, BeeEntity beeEntity);
}
