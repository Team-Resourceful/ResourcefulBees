package com.teamresourceful.resourcefulbees.common.mixin;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock extends Block {
    public MixinHoneyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource source) {
        if (Direction.stream().anyMatch(resourcefulbees$heatSourcePredicate(level, pos))) {
            this.resourcefulbees$melt(level, pos);
        }
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @NotNull
    private static Predicate<Direction> resourcefulbees$heatSourcePredicate(@NotNull ServerLevel level, @NotNull BlockPos pos) {
        return direction -> resourcefulbees$blockInDirectionIsHeatSource(level, pos, direction);
    }

    private static boolean resourcefulbees$blockInDirectionIsHeatSource(@NotNull ServerLevel level, @NotNull BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction)).is(ModTags.Blocks.HEAT_SOURCES);
    }

    private void resourcefulbees$melt(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, ModBlocks.HONEY_FLUID_BLOCK.get().defaultBlockState());
        level.neighborChanged(pos, ModBlocks.HONEY_FLUID_BLOCK.get(), pos);
    }
}
