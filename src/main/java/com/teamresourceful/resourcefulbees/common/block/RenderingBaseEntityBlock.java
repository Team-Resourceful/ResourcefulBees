package com.teamresourceful.resourcefulbees.common.block;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class RenderingBaseEntityBlock extends BaseEntityBlock {
    protected RenderingBaseEntityBlock(Properties properties) {
        super(properties);
    }

    /*
        DONT KNOW WHY VANILLA DEFAULTS TO INVISIBLE BUT SCREW THAT.
     */
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
