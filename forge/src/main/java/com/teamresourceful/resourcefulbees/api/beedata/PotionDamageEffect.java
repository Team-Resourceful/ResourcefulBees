package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class PotionDamageEffect {

    public static final Codec<PotionDamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.MOB_EFFECT.fieldOf("effect").orElse(MobEffects.BAD_OMEN).forGetter(PotionDamageEffect::getEffect),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("strength").orElse(1).forGetter(PotionDamageEffect::getStrength)
    ).apply(instance, PotionDamageEffect::new));

    private final MobEffect effect;
    private final int strength;

    private PotionDamageEffect(MobEffect effect, int strength) {
        this.effect = effect;
        this.strength = strength;
    }

    public MobEffect getEffect() {
        return effect;
    }

    public int getStrength() {
        return strength;
    }

    public static PotionDamageEffect create(MobEffect effectID) {
        return new PotionDamageEffect(effectID, 1);
    }

    public static PotionDamageEffect create(MobEffect effectID, int strength) {
        return new PotionDamageEffect(effectID, strength);
    }
}
