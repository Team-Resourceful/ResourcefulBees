package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.mixin.common.BeehiveEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public final class EntityUtils {

    private EntityUtils() {
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
                        if (entity instanceof CustomBeeEntity customBee) {
                            customBee.setPersistenceRequired();
                        }
                    }
                });
    }

    public static String getBeeColorOrDefault(Entity bee) {
        return bee instanceof CustomBee iBee ? iBee.getRenderData().colorData().jarColor().toString() : BeeConstants.VANILLA_BEE_COLOR;
    }

    public static @NotNull CompoundTag createJarBeeTag(Bee bee) {
        CompoundTag nbt = new CompoundTag();
        bee.saveAsPassenger(nbt);
        String beeColor = EntityUtils.getBeeColorOrDefault(bee);
        nbt.putString(NBTConstants.BeeJar.COLOR, beeColor);
        BeehiveEntityAccessor.callRemoveIgnoredBeeTags(nbt);
        return nbt;
    }

    public static void setEntityLocationAndAngle(BlockPos blockpos, Direction direction, Entity entity) {
        EntityDimensions size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.moveTo(d1, d2, d3, entity.getYRot(), entity.getXRot());
    }

    public static void ageBee(int ticksInHive, Animal animal) {
        int i = animal.getAge();
        if (i < 0) {
            animal.setAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            animal.setAge(Math.max(0, i - ticksInHive));
        }

        if (animal instanceof CustomBeeEntity bee) {
            bee.setLoveTime(Math.max(0, animal.getInLoveTime() - ticksInHive));
        } else {
            animal.setInLoveTime(Math.max(0, animal.getInLoveTime() - ticksInHive));
        }
        if (animal instanceof Bee bee) bee.resetTicksWithoutNectarSinceExitingHive();
    }

    public static void flagBeesInRange(BlockPos pos, Level level) {
        if (level != null) {
            level.getEntitiesOfClass(CustomBeeEntity.class, new AABB(pos).inflate(10))
                    .forEach(bee -> bee.setHasHiveInRange(true));
        }
    }
}
