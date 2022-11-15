package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.enums.AuraType;

public record BeeAura(AuraType type, DamageEffect damageEffect, PotionEffect potionEffect, int modifier, boolean calmingDisabled) {

    public static final Codec<BeeAura> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AuraType.CODEC.fieldOf("aura").forGetter(BeeAura::type),
            DamageEffect.CODEC.fieldOf("damageEffect").orElse(DamageEffect.DEFAULT).forGetter(BeeAura::damageEffect),
            PotionEffect.CODEC.fieldOf("potionEffect").orElse(PotionEffect.DEFAULT).forGetter(BeeAura::potionEffect),
            Codec.INT.fieldOf("modifier").orElse(0).forGetter(BeeAura::modifier),
            Codec.BOOL.fieldOf("calmingDisabled").orElse(false).forGetter(BeeAura::calmingDisabled)
    ).apply(instance, BeeAura::new));

    public boolean isBeneficial() {
        if (type == AuraType.POTION) return this.potionEffect().effect().isBeneficial();
        return type.isBeneficial();
    }
}
