package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Bee.class)
public interface BeeEntityAccessor {

    @Accessor
    void setHivePos(BlockPos pos);

    @Accessor
    int getStayOutOfHiveCountdown();

    @Accessor
    int getRemainingCooldownBeforeLocatingNewFlower();

    @Accessor
    void setRemainingCooldownBeforeLocatingNewFlower(int time);

    @Accessor("beePollinateGoal")
    Bee.BeePollinateGoal getPollinateGoal();

    @Accessor("beePollinateGoal")
    void setPollinateGoal(Bee.BeePollinateGoal goal);

    @Accessor
    Bee.BeeGoToHiveGoal getGoToHiveGoal();

    @Accessor
    void setGoToHiveGoal(Bee.BeeGoToHiveGoal goal);

    @Accessor
    Bee.BeeGoToKnownFlowerGoal getGoToKnownFlowerGoal();

    @Accessor
    void setGoToKnownFlowerGoal(Bee.BeeGoToKnownFlowerGoal goal);
    
}
