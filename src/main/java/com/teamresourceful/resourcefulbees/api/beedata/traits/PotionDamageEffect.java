package com.teamresourceful.resourcefulbees.api.beedata.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class PotionDamageEffect {

    public static final Codec<PotionDamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.MOB_EFFECT.fieldOf("effect").orElse(Effects.BAD_OMEN).forGetter(PotionDamageEffect::getEffect),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("strength").orElse(1).forGetter(PotionDamageEffect::getStrength)
    ).apply(instance, PotionDamageEffect::new));

    private final Effect effect;
    private final int strength;

    private PotionDamageEffect(Effect effect, int strength) {
        this.effect = effect;
        this.strength = strength;
    }

    public Effect getEffect() {
        return effect;
    }

    public int getStrength() {
        return strength;
    }

    public static PotionDamageEffect create(Effect effectID) {
        return new PotionDamageEffect(effectID, 1);
    }

    public static PotionDamageEffect create(Effect effectID, int strength) {
        return new PotionDamageEffect(effectID, strength);
    }
}
