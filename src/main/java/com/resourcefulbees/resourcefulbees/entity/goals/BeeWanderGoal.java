package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.ModBeeEntity;
import com.resourcefulbees.resourcefulbees.utils.RandomPositionGenerator;

import java.util.EnumSet;
import java.util.Objects;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class BeeWanderGoal extends Goal {

    private final ModBeeEntity modBeeEntity;
    //TODO Goodvise code start
    private Path path;
    private int tickToRefreshPath = 0;
    //TODO Goodvise code end

    public BeeWanderGoal(ModBeeEntity modBeeEntity) {
        this.modBeeEntity = modBeeEntity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return this.modBeeEntity.getRandom().nextInt(10) == 0 && this.modBeeEntity.getNavigation().isDone();
    }

    public boolean canContinueToUse() {
        return this.modBeeEntity.getNavigation().isInProgress();
    }

    public void start() {
        //TODO Goodvise code start
        if ((this.path == null || this.tickToRefreshPath++ % 10 == 0)) {

            Vector3d vector3d = this.getRandomLocation();

            if (vector3d == null) {
                return;
            }

            this.path = this.modBeeEntity.getNavigation().createPath(new BlockPos(vector3d), 1);
        }

        if (this.path != null) {
            this.modBeeEntity.getNavigation().moveTo(this.path, 1.0D);
        }
        //TODO Goodvise code end
    }

    @Nullable
    private Vector3d getRandomLocation() {
        Vector3d vector3d;
        if (this.modBeeEntity.isHiveValid() && !this.modBeeEntity.checkIsWithinDistance(Objects.requireNonNull(this.modBeeEntity.hivePos), 22)) {
            Vector3d vector3d1 = Vector3d.atCenterOf(this.modBeeEntity.hivePos);
            vector3d = vector3d1.subtract(this.modBeeEntity.position()).normalize();
        } else {
            vector3d = this.modBeeEntity.getViewVector(0.0F);
        }

        int randHorz = this.modBeeEntity.getRandom().nextInt(8) + 8;
        Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(this.modBeeEntity, randHorz, 7, vector3d);
        return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(this.modBeeEntity, randHorz, 6, -4, vector3d);
    }
}
