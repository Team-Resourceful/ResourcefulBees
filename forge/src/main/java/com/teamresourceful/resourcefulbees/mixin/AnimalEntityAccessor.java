package com.teamresourceful.resourcefulbees.mixin;

import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Animal.class)
public interface AnimalEntityAccessor {

    @Accessor("inLove")
    void setLove(int time);
}
