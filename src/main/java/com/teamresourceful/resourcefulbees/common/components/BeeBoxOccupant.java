package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;

@NullMarked
public record BeeBoxOccupant(
        TypedEntityData<EntityType<?>> entityData,
        long insertionGameTime
) implements Occupant {

    public static final Codec<BeeBoxOccupant> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TypedEntityData.codec(EntityType.CODEC)
                            .fieldOf("entity_data")
                            .forGetter(BeeBoxOccupant::entityData),
                    Codec.LONG
                            .fieldOf("insertion_game_time")
                            .forGetter(BeeBoxOccupant::insertionGameTime)
            ).apply(instance, BeeBoxOccupant::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeBoxOccupant> STREAM_CODEC =
            StreamCodec.composite(
                    TypedEntityData.streamCodec(EntityType.STREAM_CODEC),
                    BeeBoxOccupant::entityData,
                    ByteBufCodecs.VAR_LONG,
                    BeeBoxOccupant::insertionGameTime,
                    BeeBoxOccupant::new
            );

    public static BeeBoxOccupant from(Entity entity) {
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

            return new BeeBoxOccupant(
                    TypedEntityData.of(
                            entity.getType(),
                            output.buildResult()
                    ),
                    entity.level().getGameTime()
            );
        }
    }

    @Override
    public @Nullable Entity createEntity(Level level, BlockPos hivePos) {
        CompoundTag entityTag = this.entityData.copyTagWithoutId();
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(entityTag::remove);

        Entity entity = EntityType.loadEntityRecursive(
                this.entityData.type(),
                entityTag,
                level,
                EntitySpawnReason.LOAD,
                EntityProcessor.NOP
        );

        if (entity == null || !entity.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            return null;
        }

        entity.setPos(
                hivePos.getX() + 0.5D,
                hivePos.getY() + 0.5D,
                hivePos.getZ() + 0.5D
        );

        entity.setNoGravity(true);

        if (entity instanceof Bee bee) {
            setBeeReleaseData(this.ticksInBox(level), bee);
        }

        return entity;
    }

    public int ticksInBox(Level level) {
        long elapsed = Math.max(
                0L,
                level.getGameTime() - this.insertionGameTime
        );

        return (int) Math.min(elapsed, Integer.MAX_VALUE);
    }
}