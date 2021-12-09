package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoneyEffect {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<HoneyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(HoneyEffect::getEffect),
            Codec.INT.fieldOf("duration").orElse(300).forGetter(HoneyEffect::getDuration),
            Codec.INT.fieldOf("strength").orElse(0).forGetter(HoneyEffect::getStrength),
            Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(HoneyEffect::getChance)
    ).apply(instance, HoneyEffect::new));

    private final int duration;
    private final int strength;
    private final float chance;
    private final MobEffect effect;

    public HoneyEffect(MobEffect effect, int duration, int strength, float chance) {
        this.effect = effect;
        this.duration = duration;
        this.strength = strength;
        this.chance = chance;
    }

    public MobEffectInstance getInstance() {
        return new MobEffectInstance(getEffect(), duration, strength);
    }

    public MobEffect getEffect() {
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
