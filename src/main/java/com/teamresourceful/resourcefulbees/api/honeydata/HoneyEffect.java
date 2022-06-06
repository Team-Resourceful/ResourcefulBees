package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public record HoneyEffect(MobEffect effect, int duration, int strength, float chance) {

    public static final Codec<HoneyEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(HoneyEffect::effect),
            Codec.INT.fieldOf("duration").orElse(300).forGetter(HoneyEffect::duration),
            Codec.INT.fieldOf("strength").orElse(0).forGetter(HoneyEffect::strength),
            Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(HoneyEffect::chance)
    ).apply(instance, HoneyEffect::new));

    public MobEffectInstance getInstance() {
        return new MobEffectInstance(effect, duration, strength);
    }
}
