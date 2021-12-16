package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BeeHouseBlock extends RenderingBaseEntityBlock {

    public BeeHouseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState topState = level.getBlockState(pos.above());
        return topState.getMaterial().isReplaceable() || topState.getBlock() instanceof BeeHouseTopBlock;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        level.setBlock(pos.above(), ModBlocks.BEEHOUSE_TOP.get().withPropertiesOf(state), Block.UPDATE_ALL);
    }

    //We make this public so the top block can call this.
    @Override
    public void spawnDestroyParticles(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.spawnDestroyParticles(level, player, pos, state);
    }
}
