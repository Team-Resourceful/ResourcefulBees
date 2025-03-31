package com.teamresourceful.resourcefulbees.platform.common.util;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public  class LevelUtils {

    @ExpectPlatform
    public static BlockPathTypes getType(BlockState state, BlockGetter level, BlockPos pos, Mob mob) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static BlockPathTypes getType(FluidState state, BlockGetter level, BlockPos pos, Mob mob, boolean logging) {
        throw new NotImplementedException();
    }
}
