package com.teamresourceful.resourcefulbees.common.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface BlockAccessor {

    @Accessor
    boolean getHasCollision();
}
