package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;

public record TraitDamageType(String type, int amplifier) {
    public static final Codec<TraitDamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("damageType", "").forGetter(TraitDamageType::type),
            CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("amplifier", 0).forGetter(TraitDamageType::amplifier)
    ).apply(instance, TraitDamageType::new));
}
