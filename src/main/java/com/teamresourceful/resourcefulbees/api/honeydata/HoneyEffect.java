package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoneyEffect {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<HoneyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.MOB_EFFECT.fieldOf("effect").forGetter(HoneyEffect::getEffect),
            Codec.INT.fieldOf("duration").orElse(300).forGetter(HoneyEffect::getDuration),
            Codec.INT.fieldOf("strength").orElse(0).forGetter(HoneyEffect::getStrength),
            Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(HoneyEffect::getChance)
    ).apply(instance, HoneyEffect::new));

    private final int duration;
    private final int strength;
    private final float chance;
    private final Effect effect;

    public HoneyEffect(Effect effect, int duration, int strength, float chance) {
        this.effect = effect;
        this.duration = duration;
        this.strength = strength;
        this.chance = chance;
    }

    public EffectInstance getInstance() {
        return new EffectInstance(getEffect(), duration, strength);
    }

    public Effect getEffect() {
        return effect;
    }

    public int getDuration() {
        return duration;
    }

    public int getStrength() {
        return strength;
    }

    public float getChance() {
        return chance;
    }
}
