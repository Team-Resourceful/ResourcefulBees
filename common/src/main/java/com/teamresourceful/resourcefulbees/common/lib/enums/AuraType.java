package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public enum AuraType {
    BURNING(false, ParticleTypes.FLAME),
    POTION(false, ParticleTypes.WITCH),
    HEALING(true, ParticleTypes.HAPPY_VILLAGER),
    EXPERIENCE(true, ParticleTypes.ENCHANT),
    DAMAGING(false, ParticleTypes.CRIT),
    EXPERIENCE_DRAIN(false, ParticleTypes.POOF);

    public static final EnumCodec<AuraType> CODEC = EnumCodec.of(AuraType.class);

    private final boolean beneficial;
    public final SimpleParticleType particle;

    AuraType(boolean beneficial, SimpleParticleType particle) {
        this.beneficial = beneficial;
        this.particle = particle;
    }

    public boolean isBeneficial() {
        return this.beneficial;
    }
}
