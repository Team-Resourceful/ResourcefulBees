package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DamageType(String type, int amplifier) {
    public static final Codec<DamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("damageType").orElse("").forGetter(DamageType::type),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amplifier").orElse(0).forGetter(DamageType::amplifier)
    ).apply(instance, DamageType::new));
}
