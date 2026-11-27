package com.teamresourceful.resourcefulbees.api.data.honey.base;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface HoneyDataSerializer<T extends HoneyData<T>> {

    default Identifier id() {
        return Identifier.fromNamespaceAndPath(type().getNamespace(), type().getPath() + "/v" + version());
    }

    Identifier type();

    int version();

    Codec<T> codec(Identifier id);

    default @Nullable T defaultValue() {
        return null;
    }

    default boolean required() {
        return false;
    }

    @SuppressWarnings("unchecked")
    default T cast(HoneyData<?> data) {
        return (T) data;
    }

    static <T extends HoneyData<T>> HoneyDataSerializer<T> of(Identifier id, int version, Function<Identifier, Codec<T>> codec) {
        return new HoneyDataSerializer<>() {
            @Override
            public Identifier type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec(Identifier name) {
                return codec.apply(name);
            }

            @Override
            public boolean required() {
                return true;
            }
        };
    }

    static <T extends HoneyData<T>> HoneyDataSerializer<T> of(Identifier id, int version, Function<Identifier, Codec<T>> codec, @Nullable T defaultValue) {
        return new HoneyDataSerializer<>() {
            @Override
            public Identifier type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec(Identifier name) {
                return codec.apply(name);
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }

}
