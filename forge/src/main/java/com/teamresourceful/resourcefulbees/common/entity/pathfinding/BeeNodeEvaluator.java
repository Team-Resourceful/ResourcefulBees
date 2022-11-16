package com.teamresourceful.resourcefulbees.common.entity.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;

public class BeeNodeEvaluator extends FlyNodeEvaluator {

    @Override
    public @NotNull BlockPathTypes getBlockPathType(@NotNull BlockGetter level, int x, int y, int z) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPathTypes pathTypes = getRawPathType(level, mutableBlockPos.set(x, y, z), this.mob);
        if (pathTypes == BlockPathTypes.OPEN && y >= level.getMinBuildHeight() + 1) {
            BlockPathTypes pathTypes1 = getRawPathType(level, mutableBlockPos.set(x, y - 1, z), this.mob);
            if (pathTypes1 != BlockPathTypes.DAMAGE_FIRE && pathTypes1 != BlockPathTypes.LAVA) {
                if (pathTypes1 == BlockPathTypes.DAMAGE_CACTUS) {
                    pathTypes = BlockPathTypes.DAMAGE_CACTUS;
                } else if (pathTypes1 == BlockPathTypes.DAMAGE_OTHER) {
                    pathTypes = BlockPathTypes.DAMAGE_OTHER;
                } else if (pathTypes1 == BlockPathTypes.COCOA) {
                    pathTypes = BlockPathTypes.COCOA;
                } else if (pathTypes1 == BlockPathTypes.FENCE) {
                    if (!mutableBlockPos.equals(this.mob.blockPosition())) {
                        pathTypes = BlockPathTypes.FENCE;
                    }
                } else {
                    pathTypes = pathTypes1 != BlockPathTypes.WALKABLE && pathTypes1 != BlockPathTypes.OPEN && pathTypes1 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
                }
            } else {
                pathTypes = BlockPathTypes.DAMAGE_FIRE;
            }
        }

        if (pathTypes == BlockPathTypes.WALKABLE || pathTypes == BlockPathTypes.OPEN) {
            pathTypes = checkNeighbourBlocks(level, mutableBlockPos.set(x, y, z), pathTypes);
        }

        return pathTypes;
    }

    private static BlockPathTypes getRawPathType(BlockGetter level, BlockPos pos, Mob mob) {
        BlockState blockstate = level.getBlockState(pos);
        BlockPathTypes type = blockstate.getBlockPathType(level, pos, mob);
        if (type != null) {
            return type;
        } else {
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            if (blockstate.isAir()) {
                return BlockPathTypes.OPEN;
            } else if (!blockstate.is(BlockTags.TRAPDOORS) && !blockstate.is(Blocks.LILY_PAD) && !blockstate.is(Blocks.BIG_DRIPLEAF)) {
                if (blockstate.is(Blocks.POWDER_SNOW)) {
                    return BlockPathTypes.POWDER_SNOW;
                } else if (blockstate.is(Blocks.CACTUS)) {
                    return BlockPathTypes.DAMAGE_CACTUS;
                } else if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
                    return BlockPathTypes.DAMAGE_OTHER;
                } else if (blockstate.is(Blocks.HONEY_BLOCK)) {
                    return BlockPathTypes.STICKY_HONEY;
                } else if (blockstate.is(Blocks.COCOA)) {
                    return BlockPathTypes.COCOA;
                } else {
                    FluidState fluidstate = level.getFluidState(pos);
                    BlockPathTypes nonLoggableFluidPathType = fluidstate.getBlockPathType(level, pos, mob, false);
                    if (nonLoggableFluidPathType != null) {
                        return nonLoggableFluidPathType;
                    } else if (fluidstate.is(FluidTags.LAVA)) {
                        return BlockPathTypes.LAVA;
                    } else if (isBurningBlock(blockstate)) {
                        return BlockPathTypes.DAMAGE_FIRE;
                    } else if (DoorBlock.isWoodenDoor(blockstate) && !blockstate.getValue(DoorBlock.OPEN)) {
                        return BlockPathTypes.DOOR_WOOD_CLOSED;
                    } else if (block instanceof DoorBlock && material == Material.METAL && blockstate.getValue(DoorBlock.OPEN)) {
                        return BlockPathTypes.DOOR_IRON_CLOSED;
                    } else if (block instanceof DoorBlock && blockstate.getValue(DoorBlock.OPEN)) {
                        return BlockPathTypes.DOOR_OPEN;
                    } else if (block instanceof BaseRailBlock) {
                        return BlockPathTypes.RAIL;
                    } else if (block instanceof LeavesBlock) {
                        return BlockPathTypes.LEAVES;
                    } else if (!blockstate.is(BlockTags.FENCES) && !blockstate.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
                        if (!blockstate.isPathfindable(level, pos, PathComputationType.LAND)) {
                            return BlockPathTypes.BLOCKED;
                        } else {
                            BlockPathTypes loggableFluidPathType = fluidstate.getBlockPathType(level, pos, mob, true);
                            if (loggableFluidPathType != null) {
                                return loggableFluidPathType;
                            } else {
                                return fluidstate.is(FluidTags.WATER) ? BlockPathTypes.WATER : BlockPathTypes.OPEN;
                            }
                        }
                    } else {
                        return BlockPathTypes.FENCE;
                    }
                }
            } else {
                return BlockPathTypes.TRAPDOOR;
            }
        }
    }
}
