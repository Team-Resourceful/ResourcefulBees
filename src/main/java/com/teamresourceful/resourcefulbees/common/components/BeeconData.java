package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconEffect;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashSet;
import java.util.Set;

public record BeeconData(Set<BeeconEffect> activeEffects, int range, boolean active, FluidStack fluid) {

    public static final BeeconData EMPTY = new BeeconData(Set.of(), 10, false, FluidStack.EMPTY);

    public static final Codec<BeeconData> CODEC = RecordCodecBuilder.create(i -> i.group(
            CodecExtras.set(BeeconEffect.CODEC).fieldOf("activeEffects").forGetter(BeeconData::activeEffects),
            Codec.intRange(10, 50).fieldOf("range").forGetter(BeeconData::range),
            Codec.BOOL.fieldOf("active").forGetter(BeeconData::active),
            FluidStack.CODEC.fieldOf("fluid").forGetter(BeeconData::fluid)
    ).apply(i, BeeconData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeconData> STREAM_CODEC =
            StreamCodec.composite(
                    BeeconEffect.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
                    BeeconData::activeEffects,

                    ByteBufCodecs.VAR_INT,
                    BeeconData::range,

                    ByteBufCodecs.BOOL,
                    BeeconData::active,

                    FluidStack.OPTIONAL_STREAM_CODEC,
                    BeeconData::fluid,

                    BeeconData::new
            );
}
