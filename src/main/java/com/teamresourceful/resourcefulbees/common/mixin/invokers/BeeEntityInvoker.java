package com.teamresourceful.resourcefulbees.common.mixin.invokers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeEntityInvoker {

    @Invoker
    boolean callDoesHiveHaveSpace(BlockPos pos);

    @Invoker
    void callSetFlag(int i, boolean flag);

    @Invoker
    boolean callWantsToEnterHive();

    @Invoker
    boolean callIsHiveNearFire();

    @Invoker
    boolean callIsTiredOfLookingForNectar();
}
