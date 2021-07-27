package com.teamresourceful.resourcefulbees.entity.goals;

import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;

import java.util.EnumSet;

public class BeeTemptGoal extends Goal {
    private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).range(10.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();
    protected final CustomBeeEntity beeEntity;
    private final double speed;
    protected PlayerEntity closestPlayer;
    private int delayTemptCounter;


    public BeeTemptGoal(CustomBeeEntity beeEntity, double speedIn) {
        this.beeEntity = beeEntity;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(beeEntity.getNavigation() instanceof GroundPathNavigator) && !(beeEntity.getNavigation() instanceof FlyingPathNavigator)) {
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
        } else {
            this.closestPlayer = this.beeEntity.level.getNearestPlayer(ENTITY_PREDICATE, this.beeEntity);
            if (this.closestPlayer == null) {
                return false;
            } else {
                return this.isTempting(this.closestPlayer.getMainHandItem()) || this.isTempting(this.closestPlayer.getOffhandItem());
            }
        }
    }

    protected boolean isTempting(ItemStack stack) {
        return this.beeEntity.getBreedData().getFeedItems().contains(stack.getItem());
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
        this.beeEntity.getNavigation().stop();
        this.delayTemptCounter = 100;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        this.beeEntity.getLookControl().setLookAt(this.closestPlayer, (float) (this.beeEntity.getMaxHeadYRot() + 20), (float) this.beeEntity.getMaxHeadXRot());
        if (this.beeEntity.distanceToSqr(this.closestPlayer) < 6.25D) {
            this.beeEntity.getNavigation().stop();
        } else {
            this.beeEntity.getNavigation().moveTo(this.closestPlayer, this.speed);
        }

    }
}
