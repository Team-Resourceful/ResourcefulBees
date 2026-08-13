package com.teamresourceful.resourcefulbees.common.lib.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.util.CodecUtils;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RestrictedEntityPredicate(
        @NotNull EntityType<?> entityType,
        Optional<LocationPredicate> location,
        Optional<MobEffectsPredicate> effects,
        Optional<DataComponentPatch> components,
        Optional<EntityFlagsPredicate> flags,
        Optional<EntityPredicate> targetedEntity
) {

    public static final Codec<RestrictedEntityPredicate> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ENTITY_TYPE
                            .byNameCodec()
                            .fieldOf("type")
                            .forGetter(RestrictedEntityPredicate::entityType),

                    LocationPredicate.CODEC
                            .optionalFieldOf("location")
                            .forGetter(RestrictedEntityPredicate::location),

                    MobEffectsPredicate.CODEC
                            .optionalFieldOf("effects")
                            .forGetter(RestrictedEntityPredicate::effects),

                    DataComponentPatch.CODEC
                            .optionalFieldOf("components")
                            .forGetter(RestrictedEntityPredicate::components),

                    EntityFlagsPredicate.CODEC
                            .optionalFieldOf("flags")
                            .forGetter(RestrictedEntityPredicate::flags),

                    EntityPredicate.CODEC
                            .optionalFieldOf("target")
                            .forGetter(RestrictedEntityPredicate::targetedEntity)
            ).apply(instance, RestrictedEntityPredicate::new));

    public Optional<DataComponentPatch> getComponents() {
        return this.components;
    }

    public boolean matches(ServerLevel level, Entity entity) {
        if (entity == null || entity.getType() != this.entityType) {
            return false;
        }

        if (this.location
                .filter(predicate -> !predicate.matches(
                        level,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ()
                ))
                .isPresent()) {
            return false;
        }

        if (this.effects
                .filter(predicate -> !predicate.matches(entity))
                .isPresent()) {
            return false;
        }

        if (this.components
                .filter(patch -> !CodecUtils.matchesComponents(patch, entity))
                .isPresent()) {
            return false;
        }

        if (this.flags
                .filter(predicate -> !predicate.matches(entity))
                .isPresent()) {
            return false;
        }

        Entity target = entity instanceof Mob mob
                ? mob.getTarget()
                : null;

        return this.targetedEntity
                .map(predicate -> predicate.matches(level, null, target))
                .orElse(true);
    }

}
