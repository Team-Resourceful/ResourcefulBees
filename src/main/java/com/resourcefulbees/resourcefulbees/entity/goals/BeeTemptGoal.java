package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;

import java.util.EnumSet;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Flag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;

public class BeeTemptGoal extends Goal {

    private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).range(10.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();

    protected final CustomBeeEntity beeEntity;
    private final double speed;
    protected PlayerEntity closestPlayer;
    private int delayTemptCounter;

    public BeeTemptGoal(CustomBeeEntity beeEntity, double speedIn) {
        this.beeEntity = beeEntity;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(beeEntity.getNavigation() instanceof GroundPathNavigator) && !(beeEntity.getNavigation() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    public boolean canUse() {
        //TODO Goodvise code start
        if (this.delayTemptCounter++ % 100 == 0) {

            this.closestPlayer = this.beeEntity.level.getNearestPlayer(ENTITY_PREDICATE, this.beeEntity);

            if (this.closestPlayer != null) {
                return this.isTempting(this.closestPlayer.getMainHandItem()) || this.isTempting(this.closestPlayer.getOffhandItem());
            }
        }

        return false;
        //TODO Goodvise code end

        /* TODO Old code:
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
         */
    }

    protected boolean isTempting(ItemStack stack) {
        return this.beeEntity.getBeeData().getTraitData().getSpecialAbilities().contains("clingy")
                || BeeInfoUtils.isValidBreedItem(stack, this.beeEntity.getBeeData().getBreedData());
    }

    public boolean canContinueToUse() {
        return this.canUse();
    }

    public void stop() {
        this.closestPlayer = null;
        this.beeEntity.getNavigation().stop();
        /* TODO Goodvise remove code
        this.delayTemptCounter = 100;
         */
    }

    public void tick() {
        this.beeEntity.getLookControl().setLookAt(this.closestPlayer, (float) (this.beeEntity.getMaxHeadYRot() + 20), (float) this.beeEntity.getMaxHeadXRot());
        if (this.beeEntity.distanceToSqr(this.closestPlayer) < 6.25D) {
            this.beeEntity.getNavigation().stop();
        } else {
            this.beeEntity.getNavigation().moveTo(this.closestPlayer, this.speed);
        }
    }
}
