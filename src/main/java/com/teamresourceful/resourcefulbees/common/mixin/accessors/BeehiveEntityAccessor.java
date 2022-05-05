package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public interface BeehiveEntityAccessor {

    @Accessor("stored")
    List<BeehiveBlockEntity.BeeData> getBees();

    @Invoker("removeIgnoredBeeTags")
    static void callRemoveIgnoredBeeTags(CompoundTag tag) {
        throw new AssertionError("callRemoveIgnoredBeeTags mixin did not apply!");
    }
}
