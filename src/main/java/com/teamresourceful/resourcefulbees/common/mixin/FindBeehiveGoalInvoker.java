package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeeEntity.FindBeehiveGoal.class)
public interface FindBeehiveGoalInvoker {
    
    @Invoker
    boolean callHasReachedTarget(BlockPos pos);
}
