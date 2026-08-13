package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.config.EnderBeeconConfig;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.function.Supplier;

public enum BeeconEffect {

    CALMING(() -> BuiltInRegistries.MOB_EFFECT.get(ModEffects.CALMING.getId()).orElseThrow(), EnderBeeconConfig.beeconCalmingValue),
    WATER_BREATHING(() -> MobEffects.WATER_BREATHING, EnderBeeconConfig.beeconWaterBreathingValue),
    FIRE_RESISTANCE(() -> MobEffects.FIRE_RESISTANCE, EnderBeeconConfig.beeconFireResistanceValue),
    REGENERATION(() -> MobEffects.REGENERATION, EnderBeeconConfig.beeconRegenerationValue);

    public static final Codec<BeeconEffect> CODEC = EnumCodec.of(BeeconEffect.class);

    private static final BeeconEffect[] VALUES = values();

    public static final StreamCodec<ByteBuf, BeeconEffect> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(
                    ordinal -> {
                        if (ordinal < 0 || ordinal >= VALUES.length) {
                            throw new IllegalArgumentException("Invalid BeeconEffect ordinal: " + ordinal);
                        }
                        return VALUES[ordinal];
                    },
                    BeeconEffect::ordinal
            );

    private final Supplier<Holder<MobEffect>> effectHolder;
    private final double drainAmount;

    BeeconEffect(Supplier<Holder<MobEffect>> effectHolder, double drainAmount) {
        this.effectHolder = effectHolder;
        this.drainAmount = drainAmount;
    }

    public Holder<MobEffect> effectHolder() {
        return effectHolder.get();
    }

    public MobEffect effect() {
        return this.effectHolder.get().value();
    }

    public double drainAmount() {
        return this.drainAmount;
    }
}
