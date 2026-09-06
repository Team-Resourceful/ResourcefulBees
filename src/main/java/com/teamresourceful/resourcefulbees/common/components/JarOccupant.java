package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;

import javax.annotation.Nullable;
import java.util.Optional;

public record JarOccupant(
    Optional<TypedEntityData<EntityType<?>>> entityData,
    EntityType<?> entityType,
    long insertionGameTime,
    Component displayName,
    int color
) implements Occupant {
    public static final JarOccupant EMPTY = new JarOccupant(
            Optional.empty(),
            EntityTypes.PIG,
            0,
            Component.literal("Empty"),
            EntityUtils.getBeeColorOrDefault(null)
    );

    public static final Codec<JarOccupant> CODEC = RecordCodecBuilder.create(i -> i.group(
            TypedEntityData.codec(EntityType.CODEC).optionalFieldOf("entity_data").forGetter(JarOccupant::entityData),
            EntityType.CODEC.fieldOf("entity_type").forGetter(JarOccupant::entityType),
            Codec.LONG.fieldOf("insertion_game_time").forGetter(JarOccupant::insertionGameTime),
            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(JarOccupant::displayName),
            Codec.INT.fieldOf("color").forGetter(JarOccupant::color)
    ).apply(i, JarOccupant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JarOccupant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(TypedEntityData.streamCodec(EntityType.STREAM_CODEC)),
            JarOccupant::entityData,
            EntityType.STREAM_CODEC,
            JarOccupant::entityType,
            ByteBufCodecs.VAR_LONG,
            JarOccupant::insertionGameTime,
            ComponentSerialization.STREAM_CODEC,
            JarOccupant::displayName,
            ByteBufCodecs.VAR_INT,
            JarOccupant::color,
            JarOccupant::new
    );

    public static JarOccupant from(Entity entity) {
        JarOccupant occupant;
        try (var reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), ModConstants.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);
            CompoundTag entityTag = output.buildResult();
            occupant = new JarOccupant(
                    Optional.of(TypedEntityData.of(entity.getType(), entityTag)),
                    entity.getType(),
                    entity.level().getGameTime(),
                    entity.getName().copy(),
                    EntityUtils.getBeeColorOrDefault(entity)
            );
        }

        return occupant;
    }

    public static JarOccupant from(EntityType<?> type, int color) {
        return new JarOccupant(
                Optional.empty(),
                type,
                0,
                type.getDescription().copy(),
                color
        );
    }

    public @Nullable Entity createEntity(Level level, BlockPos hivePos) {
        if (this.entityData.isEmpty()) return null;
        var data = this.entityData.get();
        CompoundTag entityTag = data.copyTagWithoutId();
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(entityTag::remove);
        Entity entity = EntityType.loadEntityRecursive(data.type(), entityTag, level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
        if (entity != null && entity.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            entity.setNoGravity(true);
            if (entity instanceof Bee bee) {
                setBeeReleaseData((int) (level.getGameTime() - insertionGameTime), bee);
            }

            return entity;
        } else {
            return null;
        }
    }

    public @Nullable Entity createEntity(Level level, EntitySpawnReason spawnReason) {
        return entityType.create(level, spawnReason);
    }
}
