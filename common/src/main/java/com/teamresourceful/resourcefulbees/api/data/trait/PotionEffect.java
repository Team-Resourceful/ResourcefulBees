package com.teamresourceful.resourcefulbees.api.data.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public record PotionEffect(MobEffect effect, int strength) {

    public static final PotionEffect DEFAULT = new PotionEffect(MobEffects.LUCK, 1);

    public static final Codec<PotionEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").orElse(MobEffects.LUCK).forGetter(PotionEffect::effect),
            Codec.intRange(0, 4).fieldOf("strength").orElse(1).forGetter(PotionEffect::strength)
    ).apply(instance, PotionEffect::new));

    public MobEffectInstance createInstance(int duration) {
        return new MobEffectInstance(effect(), duration, strength());
    }
}
