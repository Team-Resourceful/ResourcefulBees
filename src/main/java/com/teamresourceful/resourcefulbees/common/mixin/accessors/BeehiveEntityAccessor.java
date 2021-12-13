package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public interface BeehiveEntityAccessor {

    @Accessor("stored")
    List<BeehiveBlockEntity.BeeData> getBees();

    @Invoker("tickOccupants")
    static void callTickOccupants(Level level, BlockPos blockPos, BlockState state, List<BeehiveBlockEntity.BeeData> beeDataList, @Nullable BlockPos savedFlowerPos) {
        throw new AssertionError("callTickOccupants mixin did not apply!");
    }

    @Invoker("removeIgnoredBeeTags")
    static void callRemoveIgnoredBeeTags(CompoundTag tag) {
        throw new AssertionError("callRemoveIgnoredBeeTags mixin did not apply!");
    }
}
