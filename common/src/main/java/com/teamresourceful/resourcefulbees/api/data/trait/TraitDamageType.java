package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TraitDamageType(String type, int amplifier) {
    public static final Codec<TraitDamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("damageType").orElse("").forGetter(TraitDamageType::type),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amplifier").orElse(0).forGetter(TraitDamageType::amplifier)
    ).apply(instance, TraitDamageType::new));
}
