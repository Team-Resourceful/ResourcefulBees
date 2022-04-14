package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NbtPredicate.class)
public interface NbtPredicateAccessor {

    @Accessor
    CompoundTag getTag();
}
