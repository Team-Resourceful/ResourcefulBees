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

public record BeeconClient(Set<BeeconEffect> activeEffects, int range, boolean active, FluidStack fluid) {

    public static final BeeconClient EMPTY = new BeeconClient(Set.of(), 10, false, FluidStack.EMPTY);

    public static final Codec<BeeconClient> CODEC = RecordCodecBuilder.create(i -> i.group(
            CodecExtras.set(BeeconEffect.CODEC).fieldOf("activeEffects").forGetter(BeeconClient::activeEffects),
            Codec.intRange(0, 10).fieldOf("range").forGetter(BeeconClient::range),
            Codec.BOOL.fieldOf("active").forGetter(BeeconClient::active),
            FluidStack.CODEC.fieldOf("clientFluid").forGetter(BeeconClient::fluid)
    ).apply(i, BeeconClient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeconClient> STREAM_CODEC =
            StreamCodec.composite(
                    BeeconEffect.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
                    BeeconClient::activeEffects,

                    ByteBufCodecs.VAR_INT,
                    BeeconClient::range,

                    ByteBufCodecs.BOOL,
                    BeeconClient::active,

                    FluidStack.OPTIONAL_STREAM_CODEC,
                    BeeconClient::fluid,

                    BeeconClient::new
            );
}
