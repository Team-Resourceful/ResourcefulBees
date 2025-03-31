package com.teamresourceful.resourcefulbees.common.entities.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class BeeTemptGoal extends Goal {
    private static final TargetingConditions ENTITY_PREDICATE = (TargetingConditions.forNonCombat()).range(10.0D).ignoreInvisibilityTesting().ignoreLineOfSight();
    protected final Bee bee;
    private final double speed;
    protected Player closestPlayer;
    private int delayTemptCounter;


    public BeeTemptGoal(Bee bee, double speedIn) {
        this.bee = bee;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(bee.getNavigation() instanceof GroundPathNavigation) && !(bee.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        }
        this.closestPlayer = this.bee.level().getNearestPlayer(ENTITY_PREDICATE, this.bee);
        return this.closestPlayer != null && (bee.isFood(this.closestPlayer.getMainHandItem()) || bee.isFood(this.closestPlayer.getOffhandItem()));
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void stop() {
        this.closestPlayer = null;
        this.bee.getNavigation().stop();
        this.delayTemptCounter = 100;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        this.bee.getLookControl().setLookAt(this.closestPlayer, (this.bee.getMaxHeadYRot() + 20), this.bee.getMaxHeadXRot());
        if (this.bee.distanceToSqr(this.closestPlayer) < 6.25D) {
            this.bee.getNavigation().stop();
        } else {
            this.bee.getNavigation().moveTo(this.closestPlayer, this.speed);
        }

    }
}
