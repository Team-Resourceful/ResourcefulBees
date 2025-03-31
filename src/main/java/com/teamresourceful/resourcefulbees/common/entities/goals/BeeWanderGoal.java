package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.common.util.RandomPositionGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public class BeeWanderGoal extends Goal {
    private final Bee bee;

    public BeeWanderGoal(Bee bee) {
        this.bee = bee;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return bee.getNavigation().isDone() && bee.getRandom().nextInt(10) == 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        return bee.getNavigation().isInProgress();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        Vec3 vector3d = this.getRandomLocation();
        if (vector3d != null) {
            bee.getNavigation().moveTo(bee.getNavigation().createPath(BlockPos.containing(vector3d), 1), 1.0D);
        }

    }

    @Nullable
    private Vec3 getRandomLocation() {
        Vec3 vector3d;
        if (bee.isHiveValid() && !checkIsWithinDistance(bee, Objects.requireNonNull(bee.getHivePos()), 22)) {
            Vec3 vector3d1 = Vec3.atCenterOf(bee.getHivePos());
            vector3d = vector3d1.subtract(bee.position()).normalize();
        } else {
            vector3d = bee.getViewVector(0.0F);
        }

        int randHorz = bee.getRandom().nextInt(8) + 8;

        Vec3 vector3d2 = RandomPositionGenerator.findAirTarget(bee, randHorz, 7, vector3d);
        return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(bee, randHorz, 6, -4, vector3d);
    }

    public static boolean checkIsWithinDistance(Bee bee, @NotNull BlockPos hivePos, int i) {
        return hivePos.closerThan(bee.blockPosition(), i);
    }
}
