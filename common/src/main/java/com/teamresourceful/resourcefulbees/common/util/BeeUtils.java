package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class BeeUtils {

    private BeeUtils() {
        throw new UtilityClassError();
    }

    public static void summonEntity(CompoundTag tag, Level level, Player player, BlockPos pos) {
        if (tag == null) return;
        EntityType.by(tag)
                .map(type -> type.create(level))
                .ifPresent(entity -> {
                    entity.load(tag);
                    entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                    level.addFreshEntity(entity);
                    if (entity instanceof Bee bee) {
                        bee.setSavedFlowerPos(null);
                        ((BeeEntityAccessor) bee).setHivePos(null);
                        if (bee.isAngry()) {
                            bee.setTarget(player);
                        }
                    }
                });
    }
}
