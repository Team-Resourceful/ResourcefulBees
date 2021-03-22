package com.resourcefulbees.resourcefulbees.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeEntityAccessor {

    @Invoker
    boolean callDoesHiveHaveSpace(BlockPos pos);

    @Invoker
    void callSetFlag(int i, boolean flag);
}
