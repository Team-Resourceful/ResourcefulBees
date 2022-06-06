package com.teamresourceful.resourcefulbees.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public record RestrictedEntityPredicate(EntityType<?> entityType, LocationPredicate location, MobEffectsPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityPredicate targetedEntity) {

    public static final Codec<RestrictedEntityPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Registry.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(RestrictedEntityPredicate::entityType),
        CodecUtils.passthrough(LocationPredicate::serializeToJson, LocationPredicate::fromJson).fieldOf("location").orElse(LocationPredicate.ANY).forGetter(RestrictedEntityPredicate::location),
        CodecUtils.passthrough(MobEffectsPredicate::serializeToJson, MobEffectsPredicate::fromJson).fieldOf("effects").orElse(MobEffectsPredicate.ANY).forGetter(RestrictedEntityPredicate::effects),
        NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(RestrictedEntityPredicate::nbt),
        CodecUtils.passthrough(EntityFlagsPredicate::serializeToJson, EntityFlagsPredicate::fromJson).fieldOf("flags").orElse(EntityFlagsPredicate.ANY).forGetter(RestrictedEntityPredicate::flags),
        CodecUtils.passthrough(EntityPredicate::serializeToJson, EntityPredicate::fromJson).fieldOf("target").orElse(EntityPredicate.ANY).forGetter(RestrictedEntityPredicate::targetedEntity)
    ).apply(instance, RestrictedEntityPredicate::new));

    public Optional<CompoundTag> getTag() {
        if (nbt() == null) return Optional.empty();
        return Optional.ofNullable(nbt().tag());
    }

    public boolean matches(ServerLevel level, Entity entity) {
        if (entityType == null) {
            return false;
        } else if (this.entityType != entity.getType()) {
            return false;
        } else if (!this.location.matches(level, entity.getX(), entity.getY(), entity.getZ())) {
            return false;
        } else if (!this.effects.matches(entity)) {
            return false;
        } else if (!this.nbt.matches(entity)) {
            return false;
        } else if (!this.flags.matches(entity)) {
            return false;
        } else {
            return this.targetedEntity.matches(level, null, entity instanceof Mob mob ? mob.getTarget() : null);
        }
    }
}
