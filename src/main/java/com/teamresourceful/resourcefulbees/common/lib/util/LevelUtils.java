package com.teamresourceful.resourcefulbees.common.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;

public class LevelUtils {


    public static PathType getType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) {
        return state.getBlockPathType(level, pos, mob);
    }


    public static PathType getType(FluidState state, BlockGetter level, BlockPos pos, Mob mob, boolean logging) {
        return state.getBlockPathType(level, pos, mob, logging);
    }
}
