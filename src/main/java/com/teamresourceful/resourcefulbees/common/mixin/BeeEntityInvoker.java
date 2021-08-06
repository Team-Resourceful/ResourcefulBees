package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeeEntity.class)
public interface BeeEntityInvoker {

    @Invoker
    boolean callDoesHiveHaveSpace(BlockPos pos);

    @Invoker
    void callSetFlag(int i, boolean flag);

    @Invoker
    boolean callIsHiveValid();

    @Invoker
    boolean callWantsToEnterHive();

    @Invoker
    boolean callIsHiveNearFire();

    @Invoker
    boolean callIsTiredOfLookingForNectar();
}
