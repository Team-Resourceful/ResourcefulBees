package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class EntityUtils {

    private EntityUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void summonEntity(CompoundTag tag, Level level, Player player, BlockPos pos) {
/*        if (tag == null) return;
        EntityType.by(tag)
                .map(type -> type.create(level))
                .ifPresent(entity -> {
                    entity.load(tag);
                    entity.(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                    level.addFreshEntity(entity);
                    if (entity instanceof Bee bee) {
                        bee.setSavedFlowerPos(null);
                        ((BeeEntityAccessor) bee).setHivePos(null);
                        if (bee.isAngry()) {
                            bee.setTarget(player);
                        }
                        if (entity instanceof CustomBeeEntity customBee) {
                            customBee.setPersistenceRequired();
                        }
                    }
                });*/
    }

    public static int getBeeColorOrDefault(Entity bee) {
        return bee instanceof CustomBee iBee ? iBee.getRenderData().colorData().jarColor().getOpaqueValue() : BeeConstants.VANILLA_BEE_INT_COLOR;
    }

    public static void setEntityLocationAndAngle(BlockPos blockpos, Direction direction, Entity entity) {
        EntityDimensions size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width() / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height() / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.snapTo(d1, d2, d3, entity.getYRot(), entity.getXRot());
    }

    public static void flagBeesInRange(BlockPos pos, Level level) {
        if (level != null) {
            level.getEntitiesOfClass(CustomBeeEntity.class, new AABB(pos).inflate(10))
                    .forEach(bee -> bee.setHasHiveInRange(true));
        }
    }
}
