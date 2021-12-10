package com.teamresourceful.resourcefulbees.common.mixin.invokers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.BeeGoToHiveGoal.class)
public interface FindBeehiveGoalInvoker {
    
    @Invoker
    boolean callHasReachedTarget(BlockPos pos);
}
