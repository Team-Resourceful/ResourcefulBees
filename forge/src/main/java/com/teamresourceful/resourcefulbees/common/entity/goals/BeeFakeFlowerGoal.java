package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BeeFakeFlowerGoal extends Goal {

    private final ResourcefulBee bee;
    private int travellingTicks = 0;
    private int ticksStuck = 0;

    private Path lastPath = null;

    public BeeFakeFlowerGoal(ResourcefulBee bee) {
        this.bee = bee;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return bee.hasFakeFlower() && bee.hasNectar() && bee.getNumberOfMutations() < bee.getMutationData().count();
    }

    public void tick() {
        System.out.println("bonk");
        if (bee.getFakeFlowerPos() != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(600)) {
                bee.dropFakeFlower();
            } else if (!bee.getNavigation().isInProgress()) {
                if (!bee.blockPosition().closerThan(bee.getFakeFlowerPos(), 16)) {
                    if (isTooFarAway(bee.getFakeFlowerPos())) {
                        bee.dropFakeFlower();
                    } else {
                        this.pathfindRandomlyTowards(bee.getFakeFlowerPos());
                    }
                } else {
                    boolean flag = this.pathfindDirectlyTowards(bee.getFakeFlowerPos());
                    if (!flag) {
                        bee.dropOffMutations();
                    } else if (this.lastPath != null && bee.getNavigation().getPath().sameAs(this.lastPath)) {
                        ++this.ticksStuck;
                        if (this.ticksStuck > 60) {
                            bee.dropFakeFlower();
                            this.ticksStuck = 0;
                        }
                    } else {
                        this.lastPath = bee.getNavigation().getPath();
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        this.travellingTicks = 0;
        this.ticksStuck = 0;
    }

    boolean isTooFarAway(BlockPos pos) {
        return !bee.blockPosition().closerThan(pos, 32);
    }

    private boolean pathfindDirectlyTowards(BlockPos pos) {
        bee.getNavigation().setMaxVisitedNodesMultiplier(10.0F);
        bee.getNavigation().moveTo((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), 1.0);
        return bee.getNavigation().getPath() != null && bee.getNavigation().getPath().canReach();
    }

    private void pathfindRandomlyTowards(BlockPos pos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(pos);
        int i = 0;
        BlockPos blockpos = bee.blockPosition();
        int j = (int) vec3.y - blockpos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int i1 = blockpos.distManhattan(pos);
        if (i1 < 15) {
            k = i1 / 2;
            l = i1 / 2;
        }

        Vec3 vec31 = AirRandomPos.getPosTowards(bee, k, l, i, vec3, 0.3141592741012573);
        if (vec31 != null) {
            bee.getNavigation().setMaxVisitedNodesMultiplier(0.5F);
            bee.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, 1.0);
        }
    }
}
