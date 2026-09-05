package com.teamresourceful.resourcefulbees.common.components;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;

import javax.annotation.Nullable;

public interface Occupant {

    @Nullable Entity createEntity(Level level, BlockPos hivePos);

    static TypedEntityData<EntityType<?>> saveEntity(Entity entity) {
        try (var reporter = new ProblemReporter.ScopedCollector(
                entity.problemPath(),
                ModConstants.LOGGER
        )) {
            var output = TagValueOutput.createWithContext(
                    reporter,
                    entity.registryAccess()
            );

            entity.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);

            return TypedEntityData.of(
                    entity.getType(),
                    output.buildResult()
            );
        }
    }

    @Nullable
    static Entity loadEntity(
            TypedEntityData<EntityType<?>> data,
            Level level
    ) {
        CompoundTag tag = data.copyTagWithoutId();
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(tag::remove);

        Entity entity = EntityType.loadEntityRecursive(
                data.type(),
                tag,
                level,
                EntitySpawnReason.LOAD,
                EntityProcessor.NOP
        );

        return entity != null && entity.is(EntityTypeTags.BEEHIVE_INHABITORS)
                ? entity
                : null;
    }

    default void setBeeReleaseData(int ticksInContainer, Bee bee) {
        updateBeeAge(ticksInContainer, bee);
        bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - ticksInContainer));
        if (bee instanceof CustomBeeEntity customBee) customBee.setLoveTime(Math.max(0, bee.getInLoveTime() - ticksInContainer));
    }

    default void updateBeeAge(int ticksInContainer, Bee bee) {
        if (!bee.isAgeLocked()) {
            int age = bee.getAge();
            if (age < 0) {
                bee.setAge(Math.min(0, age + ticksInContainer));
            } else if (age > 0) {
                bee.setAge(Math.max(0, age - ticksInContainer));
            }
        }
    }
}
