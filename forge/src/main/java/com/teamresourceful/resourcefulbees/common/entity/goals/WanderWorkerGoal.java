package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.entity.goals.base.WorkerGoal;
import com.teamresourceful.resourcefulbees.common.util.RandomPositionGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.ToDoubleFunction;

import static com.teamresourceful.resourcefulbees.common.util.MathUtils.HALF_PI;

public class WanderWorkerGoal extends WorkerGoal {

    private Vec2 startingPos;
    private double currentWeight;
    private BlockPos currentPos;
    private int randomValue;

    private int iterations = 0;
    private boolean checkingGround;
    private boolean inDistanceOfHome;
    private final Bee bee;

    public WanderWorkerGoal(Bee bee) {
        this.bee = bee;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.bee.getNavigation().isDone() && this.bee.getRandom().nextInt(10) == 0;
    }

    @Override
    public void start() {
        super.start();
        this.iterations = 0;
        this.checkingGround = false;
        this.currentWeight = Double.NEGATIVE_INFINITY;
        this.startingPos = null;
        this.currentPos = null;

        //noinspection ConstantConditions not needed because isHiveValid checks if hivepos is null.
        if (this.bee.isHiveValid() && !this.bee.getHivePos().closerToCenterThan(this.bee.position(), 22)) {
            final Vec3 vec3 = Vec3.atCenterOf(this.bee.getHivePos()).subtract(this.bee.position()).normalize();
            this.startingPos = new Vec2((float)vec3.x, (float)vec3.z);
        } else {
            this.startingPos = calculateViewVector(this.bee.xRotO, this.bee.yRotO);
        }
        this.randomValue = this.bee.getRandom().nextInt(8) + 8;

        this.inDistanceOfHome = bee.hasRestriction() && bee.getRestrictCenter().closerThan(bee.blockPosition(), (bee.getRestrictRadius() + this.randomValue) + 1.0D);
    }

    @Override
    protected boolean hasWork() {
        return this.iterations < 9 || (this.checkingGround || this.currentPos == null);
    }

    @Override
    protected boolean doWork() {
        if (this.iterations >= 9) {
            if (this.checkingGround || this.currentPos != null) return false;
            this.checkingGround = true;
            this.iterations = 0;
        }


        if (this.checkingGround) {
            BlockPos pos = getRandomOffset(this.bee.getRandom(), randomValue, 13, 10, this.startingPos);
            if (pos != null) {
                findTarget(bee, randomValue, pos, true, bee::getWalkTargetValue);
            }
        } else {
            BlockPos pos = getRandomOffset(this.bee.getRandom(), randomValue, 15, 7, this.startingPos);
            if (pos != null) {
                findTarget(bee, randomValue, pos, false, bee::getWalkTargetValue);
            }
        }
        this.iterations++;

        return true;
    }

    @Override
    public void stop() {
        if (this.currentPos == null) return;
        this.bee.getNavigation().moveTo(this.bee.getNavigation().createPath(this.currentPos, 1), 1.0D);
    }

    protected final Vec2 calculateViewVector(float xRot, float yRot) {
        final float f1 = -yRot * 0.017453292f;
        final float f4 = Mth.cos(xRot * 0.017453292f);
        return new Vec2(Mth.sin(f1) * f4, Mth.cos(f1) * f4);
    }

    private void findTarget(PathfinderMob bee, int horizontalOffset, @NotNull BlockPos randomOffset, boolean pathOnWater, ToDoubleFunction<BlockPos> weightCalculator) {
        final PathNavigation navigation = bee.getNavigation();
        final RandomSource random = bee.getRandom();

        //if position is not null which should always be true
        int rndPosX = randomOffset.getX();
        int rndPosY = randomOffset.getY();
        int rndPosZ = randomOffset.getZ();

        if (bee.hasRestriction()) { //if bee has a home && horizontal offset is greater than 1
            final BlockPos homePos = bee.getRestrictCenter(); //get home position

            //checks if bee is east of home and sets position closer based on direction
            int nextInt = random.nextInt(horizontalOffset / 2);
            rndPosX = bee.getX() > homePos.getX() ? rndPosX - nextInt : rndPosX + nextInt;

            //checks if bee is north of home and sets position closer based on direction
            nextInt = random.nextInt(horizontalOffset / 2);
            rndPosZ = bee.getZ() > homePos.getZ() ? rndPosZ - nextInt : rndPosZ + nextInt;
        }

        //create new target block position using relative position and offset
        BlockPos targetPos = new BlockPos(rndPosX + bee.getX(), rndPosY + bee.getY(), rndPosZ + bee.getZ());

        //if target Y is between 0 and world height AND (is not in Distance of home OR target pos is in distance of home) AND entity can stand on target pos
        if (!bee.level.isOutsideBuildHeight(targetPos) && (!this.inDistanceOfHome || bee.isWithinRestriction(targetPos)) && navigation.isStableDestination(targetPos)) {

            //flip a coin heads = check block above is air if so find valid position above else go below
            if (random.nextBoolean() && bee.level.isEmptyBlock(bee.blockPosition().above())) {
                targetPos = RandomPositionGenerator.findValidPositionAbove(targetPos, random.nextInt(3) + 1, bee.level.getMaxBuildHeight(), pos -> bee.level.getBlockState(pos).getMaterial().isSolid());
            } else {
                targetPos = RandomPositionGenerator.findValidPositionBelow(targetPos, random.nextInt(3) + 1, bee.level.getMinBuildHeight(), pos -> bee.level.getBlockState(pos).getMaterial().isSolid());
            }

            // if can travel through water or target pos is not tagged as water
            if (pathOnWater || !GoalUtils.isWater(bee, targetPos)) {
                //set path node type based on target position
                if (bee.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(bee.level, targetPos.mutable())) == 0.0F) {
                    //calculate if weight of new target position is better than previous target position
                    final double d1 = weightCalculator.applyAsDouble(targetPos);
                    if (d1 > this.currentWeight) {
                        this.currentWeight = d1;
                        this.currentPos = targetPos;
                    }
                }
            }
        }
    }

    private static BlockPos getRandomOffset(final RandomSource random, final int horizontalOffset, final int randInt, final int randIntMinus, final @Nullable Vec2 vec2) {
        if (vec2 != null) {
            final double d4 = (Mth.atan2(vec2.y, vec2.x) - HALF_PI) + (2 * random.nextFloat() - 1) * HALF_PI;
            final double d0 = random.nextDouble() * Mth.SQRT_OF_TWO * horizontalOffset;
            final double d1 = -d0 * Mth.sin((float)d4);
            final double d2 = d0 * Mth.cos((float)d4);
            if ((Math.abs(d1) <= horizontalOffset) && (Math.abs(d2) <= horizontalOffset)) {
                return new BlockPos(d1, random.nextInt(randInt) - randIntMinus, d2);
            }
            return null;
        }
        final int i = random.nextInt(2 * horizontalOffset + 1) - horizontalOffset;
        final int j = random.nextInt(randInt) - randIntMinus;
        final int k = random.nextInt(2 * horizontalOffset + 1) - horizontalOffset;
        return new BlockPos(i, j, k);
    }

}
