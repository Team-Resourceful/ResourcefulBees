package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
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

public record JarOccupant(
    TypedEntityData<EntityType<?>> entityData,
    int ticksInJar,
    Component displayName,
    int color
) implements Occupant {

    public static final Codec<JarOccupant> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            TypedEntityData.codec(EntityType.CODEC).fieldOf("entity_data").forGetter(JarOccupant::entityData),
                            Codec.INT.fieldOf("ticks_in_jar").forGetter(JarOccupant::ticksInJar),
                            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(JarOccupant::displayName),
                            Codec.INT.fieldOf("color").forGetter(JarOccupant::color)
                    )
                    .apply(i, JarOccupant::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, JarOccupant> STREAM_CODEC = StreamCodec.composite(
            TypedEntityData.streamCodec(EntityType.STREAM_CODEC),
            JarOccupant::entityData,
            ByteBufCodecs.VAR_INT,
            JarOccupant::ticksInJar,
            ComponentSerialization.STREAM_CODEC,
            JarOccupant::displayName,
            ByteBufCodecs.VAR_INT,
            JarOccupant::color,
            JarOccupant::new
    );

    public static JarOccupant of(Entity entity) {
        JarOccupant occupant;
        try (var reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), ResourcefulBees.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);
            CompoundTag entityTag = output.buildResult();
            occupant = new JarOccupant(
                    TypedEntityData.of(entity.getType(), entityTag),
                    0,
                    entity.getDisplayName(),
                    EntityUtils.getBeeColorOrDefault(entity)
            );
        }

        return occupant;
    }

    public static final JarOccupant EMPTY = new JarOccupant(
            null,
            0,
            Component.literal("Empty"),
            EntityUtils.getBeeColorOrDefault(null)
    );

    public @Nullable Entity createEntity(Level level, BlockPos hivePos) {
        CompoundTag entityTag = this.entityData.copyTagWithoutId();
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(entityTag::remove);
        Entity entity = EntityType.loadEntityRecursive(this.entityData.type(), entityTag, level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
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

    public JarOccupant withTickOffSet() {
        return this.withTickOffset(1);
    }

    public JarOccupant withTickOffset(int amount) {
        return new JarOccupant(this.entityData, Math.min(this.ticksInJar + amount, Integer.MAX_VALUE - amount), this.displayName, this.color);
    }
}
