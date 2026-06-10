package com.teamresourceful.resourcefulbees.common.entities.pathfinding;

import com.teamresourceful.resourcefulbees.platform.common.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.*;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BeeNodeEvaluator extends FlyNodeEvaluator {

    public BeeNodeEvaluator() {
        super();
        setCanPassDoors(true);
    }

    @Override
    public @NotNull PathType getPathType(@NonNull PathfindingContext context, int x, int y, int z) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        PathType pathTypes = getRawPathType(context.level(), mutableBlockPos.set(x, y, z), this.mob);
        if (pathTypes == PathType.OPEN && y >= context.level().getMinBuildHeight() + 1) {
            PathType pathTypes1 = getRawPathType(context.level(), mutableBlockPos.set(x, y - 1, z), this.mob);
            if (pathTypes1 != PathType.FIRE && pathTypes1 != PathType.LAVA) {
                if (pathTypes1 == PathType.DAMAGE_CAUTIOUS) {
                    pathTypes = PathType.DAMAGE_CAUTIOUS;
                } else if (pathTypes1 == PathType.COCOA) {
                    pathTypes = PathType.COCOA;
                } else if (pathTypes1 == PathType.FENCE) {
                    if (!mutableBlockPos.equals(this.mob.blockPosition())) {
                        pathTypes = PathType.FENCE;
                    }
                } else {
                    pathTypes = pathTypes1 != PathType.WALKABLE && pathTypes1 != PathType.OPEN && pathTypes1 != PathType.WATER ? PathType.WALKABLE : PathType.OPEN;
                }
            } else {
                pathTypes = PathType.FIRE;
            }
        }

        if (pathTypes == PathType.WALKABLE || pathTypes == PathType.OPEN) {
            pathTypes = checkNeighbourBlocks(context, x, y, z, pathTypes);
        }

        return pathTypes;
    }

    private static PathType getRawPathType(PathfindingContext context, BlockPos pos, Mob mob) {
        BlockState blockstate = context.level().getBlockState(pos);
        PathType type = context.getPathTypeFromState(pos.getX(), pos.getY(), pos.getZ());
        if (type != null) {
            return type;
        } else {
            Block block = blockstate.getBlock();
            if (blockstate.isAir()) {
                return PathType.OPEN;
            } else if (!blockstate.is(BlockTags.TRAPDOORS) && !blockstate.is(Blocks.LILY_PAD) && !blockstate.is(Blocks.BIG_DRIPLEAF)) {
                if (blockstate.is(Blocks.POWDER_SNOW)) {
                    return PathType.POWDER_SNOW;
                } else if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                    return PathType.DAMAGE_CAUTIOUS;
                } else if (blockstate.is(Blocks.HONEY_BLOCK)) {
                    return PathType.STICKY_HONEY;
                } else if (blockstate.is(Blocks.COCOA)) {
                    return PathType.COCOA;
                } else {
                    FluidState fluidstate = level.getFluidState(pos);
                    PathType nonLoggableFluidPathType = LevelUtils.getType(fluidstate, level, pos, mob, false);
                    if (nonLoggableFluidPathType != null) {
                        return nonLoggableFluidPathType;
                    } else if (fluidstate.is(FluidTags.LAVA)) {
                        return PathType.LAVA;
                    } else if (isBurningBlock(blockstate)) {
                        return PathType.FIRE;
                    } else if (DoorBlock.isWoodenDoor(blockstate) && !blockstate.getValue(DoorBlock.OPEN)) {
                        return PathType.DOOR_WOOD_CLOSED;
                    } else if (block instanceof DoorBlock door && !door.type().canOpenByHand() && blockstate.getValue(DoorBlock.OPEN)) {
                        return PathType.DOOR_IRON_CLOSED;
                    } else if (block instanceof DoorBlock && blockstate.getValue(DoorBlock.OPEN)) {
                        return PathType.DOOR_OPEN;
                    } else if (block instanceof BaseRailBlock) {
                        return PathType.RAIL;
                    } else if (block instanceof LeavesBlock) {
                        return PathType.LEAVES;
                    } else if (!blockstate.is(BlockTags.FENCES) && !blockstate.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
                        if (!blockstate.isPathfindable(level, pos, PathComputationType.LAND)) {
                            return PathType.BLOCKED;
                        } else {
                            PathType loggableFluidPathType = LevelUtils.getType(fluidstate, level, pos, mob, true);
                            if (loggableFluidPathType != null) {
                                return loggableFluidPathType;
                            } else {
                                return fluidstate.is(FluidTags.WATER) ? PathType.WATER : PathType.OPEN;
                            }
                        }
                    } else {
                        return PathType.FENCE;
                    }
                }
            } else {
                return PathType.TRAPDOOR;
            }
        }
    }
}
