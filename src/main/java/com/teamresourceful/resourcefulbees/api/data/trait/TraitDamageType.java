package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;

public record TraitDamageType(String type, int amplifier) {

    public static final Codec<TraitDamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("damageType", "").forGetter(TraitDamageType::type),
            CodecExtras.NON_NEGATIVE_INT.optionalFieldOf("amplifier", 0).forGetter(TraitDamageType::amplifier)
    ).apply(instance, TraitDamageType::new));

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof TraitDamageType damageType && damageType.type.equals(type);
    }

    public static final TraitDamageType EXPLOSIVE = new TraitDamageType("explosive", 1);

    public static final TraitDamageType SET_ON_FIRE = new TraitDamageType("set_on_fire", 1);
}
