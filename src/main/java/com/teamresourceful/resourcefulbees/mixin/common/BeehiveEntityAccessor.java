package com.teamresourceful.resourcefulbees.mixin.common;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public interface BeehiveEntityAccessor {

    @Accessor("stored")
    List<BeehiveBlockEntity.BeeData> getStored();
}
