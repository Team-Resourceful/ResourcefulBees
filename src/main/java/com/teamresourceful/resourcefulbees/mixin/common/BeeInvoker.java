package com.teamresourceful.resourcefulbees.mixin.common;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeInvoker {

    @Invoker
    void callSetFlag(int i, boolean flag);

    @Invoker
    boolean callIsHiveNearFire();

    @Invoker
    boolean callIsTiredOfLookingForNectar();
}
