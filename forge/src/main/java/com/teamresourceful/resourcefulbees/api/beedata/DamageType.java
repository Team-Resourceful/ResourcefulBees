package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DamageType {
    public static final Codec<DamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("damageType").orElse("").forGetter(DamageType::getType),
            Codec.INT.fieldOf("amplifier").orElse(0).forGetter(DamageType::getAmplifier)
    ).apply(instance, DamageType::new));

    private final String type;
    private final int amplifier;

    private DamageType(String type, int amplifier) {
        this.type = type;
        this.amplifier = amplifier;
    }

    public String getType() {
        return type;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public static DamageType create(String type) {
        return new DamageType(type, 1);
    }

    public static DamageType create(String type, int amplifier) {
        return new DamageType(type, amplifier);
    }
}
