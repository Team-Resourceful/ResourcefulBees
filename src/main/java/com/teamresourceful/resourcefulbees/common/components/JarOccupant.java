package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
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
    int ticksInJar,
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
                            EntityType.CODEC.fieldOf("entityType").forGetter(JarOccupant::entityType),
                            Codec.INT.fieldOf("ticks_in_jar").forGetter(JarOccupant::ticksInJar),
                            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(JarOccupant::displayName),
                            Codec.INT.fieldOf("color").forGetter(JarOccupant::color)
                    ).apply(i, JarOccupant::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, JarOccupant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(TypedEntityData.streamCodec(EntityType.STREAM_CODEC)),
            JarOccupant::entityData,
            EntityType.STREAM_CODEC,
            JarOccupant::entityType,
            ByteBufCodecs.VAR_INT,
            JarOccupant::ticksInJar,
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
                    0,
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
                setBeeReleaseData(this.ticksInJar, bee);
            }

            return entity;
        } else {
            return null;
        }
    }

    public @Nullable Entity createEntity(Level level, EntitySpawnReason spawnReason) {
        return entityType.create(level, spawnReason);
    }

    public JarOccupant withTickOffSet() {
        return this.withTickOffset(1);
    }

    public JarOccupant withTickOffset(int amount) {
        return new JarOccupant(this.entityData, this.entityType, Math.min(this.ticksInJar + amount, Integer.MAX_VALUE - amount), this.displayName, this.color);
    }
}
