package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeeEntity.class)
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
    BeeEntity.PollinateGoal getPollinateGoal();

    @Accessor("beePollinateGoal")
    void setPollinateGoal(BeeEntity.PollinateGoal goal);

    @Accessor
    BeeEntity.FindBeehiveGoal getGoToHiveGoal();

    @Accessor
    void setGoToHiveGoal(BeeEntity.FindBeehiveGoal goal);

    @Accessor
    BeeEntity.FindFlowerGoal getGoToKnownFlowerGoal();

    @Accessor
    void setGoToKnownFlowerGoal(BeeEntity.FindFlowerGoal goal);
    
}
