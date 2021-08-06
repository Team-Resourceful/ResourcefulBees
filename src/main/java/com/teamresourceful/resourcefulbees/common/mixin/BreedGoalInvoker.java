package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BreedGoal.class)
public interface BreedGoalInvoker {
    
    @Invoker
    AnimalEntity callGetFreePartner();
}
