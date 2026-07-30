package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

//todo consider changing this to a value-based upgrade system to allow for a variety of options
public record Upgrade(Type type) {
    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
            Type.CODEC.fieldOf("type").forGetter(Upgrade::type)
    ).apply(i, Upgrade::new));

    public static final StreamCodec<ByteBuf, Upgrade> STREAM_CODEC =
            StreamCodec.composite(
                    Type.STREAM_CODEC,
                    Upgrade::type,
                    Upgrade::new
            );

    public static Upgrade create(Type type) {
        return new Upgrade(type);
    }

    public boolean isType(Type type) {
        return this.type.isType(type);
    }

    public enum Type {
        BREED_TIME,
        HONEY_CAPACITY,
        ENERGY_CAPACITY,
        ENERGY_TRANSFER,
        ENERGY_FILL;

        private static final IntFunction<Type> BY_ID =
                ByIdMap.continuous(
                        Type::ordinal,
                        values(),
                        ByIdMap.OutOfBoundsStrategy.ZERO
                );

        private static final Codec<Type> CODEC = EnumCodec.of(Type.class);

        private static final StreamCodec<ByteBuf, Type> STREAM_CODEC =
                ByteBufCodecs.idMapper(
                        BY_ID,
                        Type::ordinal
                );

        private boolean isType(Type type) {
            return this == type;
        }
    }
}
