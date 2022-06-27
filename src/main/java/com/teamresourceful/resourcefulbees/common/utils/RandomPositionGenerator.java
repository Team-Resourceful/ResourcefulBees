package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static com.teamresourceful.resourcefulbees.common.utils.MathUtils.HALF_PI;

public final class RandomPositionGenerator {

    private RandomPositionGenerator() {
        throw new IllegalArgumentException(ModConstants.UTILITY_CLASS);
    }

    //TODO clean up unnecessary logic from this class that isn't useful to the bee so it is more optimized and readable.

    @Nullable
    public static Vec3 findAirTarget(PathfinderMob creatureEntity, int i, int i1, Vec3 vector3d) {
        return findTarget(creatureEntity, i, i1, 0, vector3d, false, creatureEntity::getWalkTargetValue);
    }

    @Nullable
    public static Vec3 findGroundTarget(PathfinderMob creatureEntity, int i, int i1, int i2, @Nullable Vec3 vector3d) {
        return findTarget(creatureEntity, i, i1, i2, vector3d, true, creatureEntity::getWalkTargetValue);
    }

    @Nullable
    private static Vec3 findTarget(PathfinderMob bee, int horizontalOffset, int verticalOffset, int zero, @Nullable Vec3 vector3d, boolean pathOnWater, ToDoubleFunction<BlockPos> blockWeightOfBeePOS) {
        PathNavigation pathnavigator = bee.getNavigation();
        RandomSource random = bee.getRandom();


        //is bee within distance of home position?
        boolean inDistanceOfHome = bee.hasRestriction() && bee.getRestrictCenter().closerThan(bee.blockPosition(), (bee.getRestrictRadius() + horizontalOffset) + 1.0D);

        boolean flag1 = false;
        double d0 = Double.NEGATIVE_INFINITY;
        BlockPos beePos = bee.blockPosition();

        //loop 10 times getting the best valid path as far from the entities current position as possible
        for(int i = 0; i < 10; ++i) {
            BlockPos randomBlockpos = getRandomOffset(random, horizontalOffset, verticalOffset, zero, vector3d);   //get random offset
            if (randomBlockpos != null) { //if position is not null which should always be true
                int rndPosX = randomBlockpos.getX();
                int rndPosY = randomBlockpos.getY();
                int rndPosZ = randomBlockpos.getZ();

                if (bee.hasRestriction() && horizontalOffset > 1) { //if bee has a home && horizontal offset is greater than 1
                    BlockPos beeHomePosition = bee.getRestrictCenter(); //get home position

                    //checks if bee is east of home and sets position closer based on direction
                    int nextInt = random.nextInt(horizontalOffset / 2);
                    rndPosX = bee.getX() > beeHomePosition.getX() ? rndPosX - nextInt : rndPosX + nextInt;

                    //checks if bee is north of home and sets position closer based on direction
                    int nextInt1 = random.nextInt(horizontalOffset / 2);
                    rndPosZ = bee.getZ() > beeHomePosition.getZ() ? rndPosZ - nextInt1 : rndPosZ + nextInt1;
                }

                //create new target block position using relative position and offset
                BlockPos targetPos = new BlockPos(rndPosX + bee.getX(), rndPosY + bee.getY(), rndPosZ + bee.getZ());

                //if target Y is between 0 and world height AND (is not in Distance of home OR target pos is in distance of home) AND entity can stand on target pos
                if (MathUtils.inRangeInclusive(targetPos.getY(), bee.level.getMinBuildHeight(), bee.level.getMaxBuildHeight()) && (!inDistanceOfHome || bee.isWithinRestriction(targetPos)) && pathnavigator.isStableDestination(targetPos)) {

                    //flip a coin heads = check block above is air if so find valid position above else go below
                    if (random.nextBoolean() && bee.level.isEmptyBlock(bee.blockPosition().above())) {
                        targetPos = findValidPositionAbove(targetPos, random.nextInt(3) + 1, bee.level.getMaxBuildHeight(),
                                pos -> bee.level.getBlockState(pos).getMaterial().isSolid());
                    } else {
                        targetPos = findValidPositionBelow(targetPos, random.nextInt(3) + 1, bee.level.getMinBuildHeight(),
                                pos -> bee.level.getBlockState(pos).getMaterial().isSolid());
                    }

                    // if can travel through water or target pos is not tagged as water
                    if (pathOnWater || !bee.level.getFluidState(targetPos).is(FluidTags.WATER)) {
                        //set path node type based on target position
                        BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(bee.level, targetPos.mutable());
                        if (bee.getPathfindingMalus(pathnodetype) == 0.0F) {
                            //calculate if weight of new target position is better than previous target position
                            double d1 = blockWeightOfBeePOS.applyAsDouble(targetPos);
                            if (d1 > d0) {
                                d0 = d1;
                                beePos = targetPos;
                                flag1 = true;
                            }
                        }
                    }
                }
            }
        }

        //if target position is not equal to negative infinity then return center of target pos otherwise null
        return flag1 ? Vec3.atBottomCenterOf(beePos) : null;
    }

    private static BlockPos getRandomOffset(RandomSource random, int horizontalOffset, int verticalOffset, int minusTwo, @Nullable Vec3 directionVec) {
        if (directionVec != null) {
            double d3 = Mth.atan2(directionVec.z, directionVec.x) - HALF_PI;
            double d4 = d3 + (2 * random.nextFloat() - 1) * HALF_PI;
            double d0 = Math.sqrt(random.nextDouble()) * Mth.SQRT_OF_TWO * horizontalOffset;
            double d1 = -d0 * Math.sin(d4);
            double d2 = d0 * Math.cos(d4);
            if ((Math.abs(d1) <= horizontalOffset) && (Math.abs(d2) <= horizontalOffset)) {
                //9 - 4 - 2  = 3 = -6
                //15 - 7 - 0 = 8
                //21 - 10 - 8 = 3 = -18
                int l = random.nextInt(2 * verticalOffset + 1) - verticalOffset + minusTwo;
                return new BlockPos(d1, l, d2);
            } else {
                return null;
            }
        } else {
            int i = random.nextInt(2 * horizontalOffset + 1) - horizontalOffset;
            int j = random.nextInt(2 * verticalOffset + 1) - verticalOffset + minusTwo;
            int k = random.nextInt(2 * horizontalOffset + 1) - horizontalOffset;
            return new BlockPos(i, j, k);
        }
    }

    static BlockPos findValidPositionAbove(BlockPos blockPos3, int randInt3, int worldHeight, Predicate<BlockPos> posPredicate) {
        if (randInt3 < 0) throw new IllegalArgumentException("aboveSolidAmount was " + randInt3 + ", expected >= 0");
        if (!posPredicate.test(blockPos3)) return blockPos3;

        BlockPos blockpos = blockPos3.above();
        while (blockpos.getY() < worldHeight && posPredicate.test(blockpos)) blockpos = blockpos.above();

        BlockPos blockpos1;
        BlockPos blockpos2;
        for(blockpos1 = blockpos; blockpos1.getY() < worldHeight && blockpos1.getY() - blockpos.getY() < randInt3; blockpos1 = blockpos2) {
            blockpos2 = blockpos1.above();
            if (posPredicate.test(blockpos2)) {
                break;
            }
        }

        return blockpos1;
    }

    static BlockPos findValidPositionBelow(BlockPos blockPos3, int randInt3, int worldFloor, Predicate<BlockPos> posPredicate) {
        if (randInt3 < 0) throw new IllegalArgumentException("aboveSolidAmount was " + randInt3 + ", expected >= 0");
        if (!posPredicate.test(blockPos3)) return blockPos3;

        BlockPos blockpos = blockPos3.below();
        while (blockpos.getY() > worldFloor && posPredicate.test(blockpos)) blockpos = blockpos.below();

        BlockPos blockpos1;
        BlockPos blockpos2;
        for(blockpos1 = blockpos; blockpos1.getY() > worldFloor && blockpos.getY() - blockpos1.getY() < randInt3; blockpos1 = blockpos2) {
            blockpos2 = blockpos1.below();
            if (posPredicate.test(blockpos2)) {
                break;
            }
        }

        return blockpos1;
    }
}
