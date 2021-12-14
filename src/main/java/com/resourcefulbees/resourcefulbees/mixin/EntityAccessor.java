package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("firstTick")
    boolean getFirstTick();

    @Accessor("random")
    Random getRandom();
}
