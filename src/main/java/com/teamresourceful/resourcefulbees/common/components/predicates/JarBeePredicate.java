package com.teamresourceful.resourcefulbees.common.components.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

public record JarBeePredicate(
        Optional<EntityType<?>> entityType
) implements SingleComponentItemPredicate<JarOccupant> {

    public static final Codec<JarBeePredicate> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EntityType.CODEC
                            .optionalFieldOf("entity_type")
                            .forGetter(JarBeePredicate::entityType)
            ).apply(instance, JarBeePredicate::new));

    public static final DataComponentPredicate.Type<JarBeePredicate> TYPE =
            new DataComponentPredicate.ConcreteType<>(CODEC);

    public static JarBeePredicate any() {
        return new JarBeePredicate(Optional.empty());
    }

    public static JarBeePredicate of(EntityType<?> entityType) {
        return new JarBeePredicate(Optional.of(entityType));
    }

    @Override
    public DataComponentType<JarOccupant> componentType() {
        return ModDataComponents.JAR_BEE.get();
    }

    @Override
    public boolean matches(JarOccupant occupant) {
        return entityType
                .map(type -> occupant.entityType() == type)
                .orElse(true);
    }
}