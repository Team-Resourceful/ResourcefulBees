package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeehiveBlockEntity.BeeData.class)
public interface BTEBeeAccessor {

    @Accessor("ticksInHive")
    int getTicksInHive();
}
