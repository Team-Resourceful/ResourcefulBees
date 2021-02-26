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
    private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(10.0D).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
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
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(beeEntity.getNavigator() instanceof GroundPathNavigator) && !(beeEntity.getNavigator() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            --this.delayTemptCounter;
            return false;
        } else {
            this.closestPlayer = this.beeEntity.world.getClosestPlayer(ENTITY_PREDICATE, this.beeEntity);
            if (this.closestPlayer == null) {
                return false;
            } else {
                return this.isTempting(this.closestPlayer.getHeldItemMainhand()) || this.isTempting(this.closestPlayer.getHeldItemOffhand());
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
    public boolean shouldContinueExecuting() {
        if (this.isScaredByPlayerMovement()) {
            if (this.beeEntity.getDistanceSq(this.closestPlayer) < 36.0D) {
                if (this.closestPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.closestPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double) this.closestPlayer.rotationYaw - this.yaw) > 5.0D) {
                    return false;
                }
            } else {
                this.targetX = this.closestPlayer.getX();
                this.targetY = this.closestPlayer.getY();
                this.targetZ = this.closestPlayer.getZ();
            }


            this.pitch = this.closestPlayer.rotationPitch;
            this.yaw = this.closestPlayer.rotationYaw;
        }

        return this.shouldExecute();
    }

    protected boolean isScaredByPlayerMovement() {
        return this.scaredByPlayerMovement;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.targetX = this.closestPlayer.getX();
        this.targetY = this.closestPlayer.getY();
        this.targetZ = this.closestPlayer.getZ();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void resetTask() {
        this.closestPlayer = null;
        this.beeEntity.getNavigator().clearPath();
        this.delayTemptCounter = 100;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        this.beeEntity.getLookController().setLookPositionWithEntity(this.closestPlayer, (float) (this.beeEntity.getHorizontalFaceSpeed() + 20), (float) this.beeEntity.getVerticalFaceSpeed());
        if (this.beeEntity.getDistanceSq(this.closestPlayer) < 6.25D) {
            this.beeEntity.getNavigator().clearPath();
        } else {
            this.beeEntity.getNavigator().tryMoveToEntityLiving(this.closestPlayer, this.speed);
        }

    }
}
