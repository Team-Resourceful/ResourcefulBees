package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashSet;
import java.util.Set;

public record BeeconData(
        Set<Pair<Holder<MobEffect>, Float>> activeEffects,
        int range,
        boolean active,
        FluidStack fluid,
        EnderBeeconBlockEntity.Target target
) {

    public static final BeeconData EMPTY = new BeeconData(Set.of(), 10, false, FluidStack.EMPTY, EnderBeeconBlockEntity.Target.BEE);

    public static final Codec<BeeconData> CODEC = RecordCodecBuilder.create(i -> i.group(
            CodecExtras.set(Codec.pair(MobEffect.CODEC, Codec.FLOAT)).fieldOf("activeEffects").forGetter(BeeconData::activeEffects),
            Codec.intRange(10, 50).fieldOf("range").forGetter(BeeconData::range),
            Codec.BOOL.fieldOf("active").forGetter(BeeconData::active),
            FluidStack.CODEC.fieldOf("fluid").forGetter(BeeconData::fluid),
            EnderBeeconBlockEntity.Target.CODEC.fieldOf("target").forGetter(BeeconData::target)
    ).apply(i, BeeconData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<Holder<MobEffect>, Float>> EFFECT_STREAM_CODEC = StreamCodec.composite(
            MobEffect.STREAM_CODEC,
            Pair::getFirst,
            ByteBufCodecs.FLOAT,
            Pair::getSecond,
            Pair::of
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeconData> STREAM_CODEC = StreamCodec.composite(
            EFFECT_STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
            BeeconData::activeEffects,

            ByteBufCodecs.VAR_INT,
            BeeconData::range,

            ByteBufCodecs.BOOL,
            BeeconData::active,

            FluidStack.OPTIONAL_STREAM_CODEC,
            BeeconData::fluid,

            EnderBeeconBlockEntity.Target.STREAM_CODEC,
            BeeconData::target,

            BeeconData::new
    );
}
