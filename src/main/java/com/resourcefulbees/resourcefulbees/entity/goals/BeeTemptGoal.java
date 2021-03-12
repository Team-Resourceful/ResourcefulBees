package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
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
    private final boolean scaredByPlayerMovement;
    protected PlayerEntity closestPlayer;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double pitch;
    private double yaw;
    private int delayTemptCounter;


    public BeeTemptGoal(CustomBeeEntity beeEntity, double speedIn, boolean scaredByPlayerMovementIn) {
        this.beeEntity = beeEntity;
        this.speed = speedIn;
        this.scaredByPlayerMovement = scaredByPlayerMovementIn;
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
        return BeeInfoUtils.isValidBreedItem(stack, this.beeEntity.getBeeData().getBreedData().getFeedItem());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        if (this.isScaredByPlayerMovement()) {
            if (this.beeEntity.distanceToSqr(this.closestPlayer) < 36.0D) {
                if (this.closestPlayer.distanceToSqr(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.closestPlayer.xRot - this.pitch) > 5.0D || Math.abs((double) this.closestPlayer.yRot - this.yaw) > 5.0D) {
                    return false;
                }
            } else {
                this.targetX = this.closestPlayer.getX();
                this.targetY = this.closestPlayer.getY();
                this.targetZ = this.closestPlayer.getZ();
            }


            this.pitch = this.closestPlayer.xRot;
            this.yaw = this.closestPlayer.yRot;
        }

        return this.canUse();
    }

    protected boolean isScaredByPlayerMovement() {
        return this.scaredByPlayerMovement;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.targetX = this.closestPlayer.getX();
        this.targetY = this.closestPlayer.getY();
        this.targetZ = this.closestPlayer.getZ();
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
