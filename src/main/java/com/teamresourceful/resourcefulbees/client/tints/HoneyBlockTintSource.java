package com.teamresourceful.resourcefulbees.client.tints;

import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;

public record HoneyBlockTintSource() implements BlockTintSource {

    @Override
    public int color(BlockState state) {
        return state.getBlock() instanceof CustomHoneyBlock block ? block.color() : 0xffffffff;
    }
}
