
package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public final class CourierReturnToControllerGoal extends Goal {

    private static final double SPEED = 1.2D;
    private static final double ARRIVAL_DISTANCE_SQUARED = 1.0D;
    private static final int REPATH_INTERVAL = 20;
    private static final int RETRY_INTERVAL = 40;

    private final ResourcefulBee bee;

    private int repathCooldown;
    private int retryCooldown;
    private boolean navigationFailed;

    public CourierReturnToControllerGoal(ResourcefulBee bee) {
        this.bee = bee;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    private ColonyControllerBlockEntity getController() {
        if (!(bee.level() instanceof ServerLevel level)) {
            return null;
        }

        BlockPos pos = bee.getColonyControllerPos();

        if (pos == null || !level.hasChunkAt(pos)) {
            return null;
        }

        if (level.getBlockEntity(pos)
                instanceof ColonyControllerBlockEntity controller) {
            return controller;
        }

        return null;
    }

    private boolean canReturn() {
        if (bee.isNoAi()
                || !bee.isAlive()
                || bee.getCourierAbility() == null
                || !bee.isColonyMember()
                || !bee.canBeginCourierReturn()
                || bee.getCourierDelivery().isAssigned()
                || bee.hasCourierCargo()
                || bee.getTarget() != null
                || bee.isPollinating()) {
            return false;
        }

        ColonyControllerBlockEntity controller = getController();

        return controller != null
                && controller.canAcceptReturningCourier(bee);
    }

    @Override
    public boolean canUse() {
        if (retryCooldown > 0) {
            retryCooldown--;
            return false;
        }

        return canReturn();
    }

    @Override
    public boolean canContinueToUse() {
        return !navigationFailed && canReturn();
    }

    @Override
    public void start() {
        repathCooldown = 0;
        navigationFailed = false;
    }

    @Override
    public void tick() {
        ColonyControllerBlockEntity controller = getController();

        if (controller == null
                || !controller.canAcceptReturningCourier(bee)) {
            bee.getNavigation().stop();
            return;
        }

        BlockPos controllerPos = controller.getBlockPos();
        BlockState state = controller.getBlockState();

        if (!state.hasProperty(BeehiveBlock.FACING)) {
            failNavigation();
            return;
        }

        Direction facing = state.getValue(BeehiveBlock.FACING);
        BlockPos entrance = controllerPos.relative(facing);

        if (!bee.level().hasChunkAt(entrance)) {
            failNavigation();
            return;
        }

        if (!bee.level().getBlockState(entrance)
                .getCollisionShape(bee.level(), entrance)
                .isEmpty()) {
            failNavigation();
            return;
        }

        double x = entrance.getX() + 0.5D;
        double y = entrance.getY() + 0.5D;
        double z = entrance.getZ() + 0.5D;

        if (bee.distanceToSqr(x, y, z)
                <= ARRIVAL_DISTANCE_SQUARED) {

            bee.getNavigation().stop();

            if (controller.canAcceptReturningCourier(bee)) {
                controller.tryEnterHive(bee);
            }

            // If storage was rejected, avoid an immediate
            // repeated attempt at the entrance.
            if (!bee.isRemoved()) {
                failNavigation();
            }

            return;
        }

        if (--repathCooldown <= 0) {
            repathCooldown = REPATH_INTERVAL;

            boolean started = bee.getNavigation().moveTo(
                    x, y, z, SPEED
            );

            if (!started) {
                failNavigation();
            }
        }
    }

    private void failNavigation() {
        bee.getNavigation().stop();
        navigationFailed = true;
        retryCooldown = RETRY_INTERVAL;
    }

    @Override
    public void stop() {
        bee.getNavigation().stop();
    }
}
