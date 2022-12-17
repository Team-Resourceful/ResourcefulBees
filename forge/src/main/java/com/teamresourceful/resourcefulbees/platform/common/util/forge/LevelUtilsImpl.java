package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.platform.common.block.BlockExtension;
import com.teamresourceful.resourcefulbees.platform.common.block.FluidExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class LevelUtilsImpl {
    public static BlockPathTypes getType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) {
        if (state.getBlock() instanceof BlockExtension extension) {
            return extension.getBlockPathType(state, level, pos, mob);
        }
        return state.getBlockPathType(level, pos, mob);
    }

    public static BlockPathTypes getType(FluidState state, BlockGetter level, BlockPos pos, Mob mob, boolean logging) {
        if (state.getType() instanceof FluidExtension extension) {
            return extension.getBlockPathType(state, level, pos, mob, logging);
        }
        return state.getBlockPathType(level, pos, mob, logging);
    }
}
