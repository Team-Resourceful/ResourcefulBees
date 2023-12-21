package com.teamresourceful.resourcefulbees.common.setup.data.honeydata.bottle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleEffectData;
import com.teamresourceful.resourcefullib.common.item.LazyHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public record CustomHoneyBottleEffectData(LazyHolder<MobEffect> effect, int duration, int strength, float chance) implements HoneyBottleEffectData {

    private static final Codec<LazyHolder<MobEffect>> EFFECT_CODEC = ResourceLocation.CODEC.xmap(LazyHolder.map(BuiltInRegistries.MOB_EFFECT), LazyHolder::getId);
    public static final Codec<HoneyBottleEffectData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EFFECT_CODEC.fieldOf("effect").forGetter(HoneyBottleEffectData::effect),
            Codec.INT.optionalFieldOf("duration", 300).forGetter(HoneyBottleEffectData::duration),
            Codec.INT.optionalFieldOf("strength", 0).forGetter(HoneyBottleEffectData::strength),
            Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(HoneyBottleEffectData::chance)
    ).apply(instance, CustomHoneyBottleEffectData::new));
}
