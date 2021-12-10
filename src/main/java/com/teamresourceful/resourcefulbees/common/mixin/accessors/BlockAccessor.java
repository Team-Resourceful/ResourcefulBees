package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface BlockAccessor {

    @Accessor
    boolean getHasCollision();
}
