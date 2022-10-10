package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeAura;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public enum AuraType {
    BURNING(false, ParticleTypes.FLAME),
    POTION(false, ParticleTypes.WITCH),
    HEALING(true, ParticleTypes.HAPPY_VILLAGER),
    EXPERIENCE(true, ParticleTypes.ENCHANT),
    DAMAGING(false, ParticleTypes.CRIT),
    EXPERIENCE_DRAIN(false, ParticleTypes.POOF);

    public static final Codec<AuraType> CODEC = Codec.STRING.xmap(AuraType::valueOf, AuraType::toString);

    private final boolean beneficial;
    public final SimpleParticleType particle;

    AuraType(boolean beneficial, SimpleParticleType particle) {
        this.beneficial = beneficial;
        this.particle = particle;
    }

    public boolean isBeneficial(BeeAura aura) {
        if (this == POTION) return aura.potionEffect().effect().isBeneficial();
        return this.beneficial;
    }
}
