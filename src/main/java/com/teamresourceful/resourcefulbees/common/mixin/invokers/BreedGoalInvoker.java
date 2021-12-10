package com.teamresourceful.resourcefulbees.common.mixin.invokers;

import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BreedGoal.class)
public interface BreedGoalInvoker {
    
    @Invoker
    Animal callGetFreePartner();
}
