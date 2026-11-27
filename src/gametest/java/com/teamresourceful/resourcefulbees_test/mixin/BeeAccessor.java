package com.teamresourceful.resourcefulbees_test.mixin;

import net.minecraft.world.entity.animal.bee.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeAccessor {

    @Invoker("setHasNectar")
    void resourcefulbees$invokeSetHasNectar(boolean hasNectar);
}