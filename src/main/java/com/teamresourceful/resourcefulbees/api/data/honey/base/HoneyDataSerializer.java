package com.teamresourceful.resourcefulbees.api.data.honey.base;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface HoneyDataSerializer<T extends HoneyData<T>> {

    default ResourceLocation id() {
        return new ResourceLocation(type().getNamespace(), type().getPath() + "/v" + version());
    }

    ResourceLocation type();

    int version();

    Codec<T> codec(String id);

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

    static <T extends HoneyData<T>> HoneyDataSerializer<T> of(ResourceLocation id, int version, Function<String, Codec<T>> codec) {
        return new HoneyDataSerializer<>() {
            @Override
            public ResourceLocation type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec(String name) {
                return codec.apply(name);
            }

            @Override
            public boolean required() {
                return true;
            }
        };
    }

    static <T extends HoneyData<T>> HoneyDataSerializer<T> of(ResourceLocation id, int version, Function<String, Codec<T>> codec, @Nullable T defaultValue) {
        return new HoneyDataSerializer<>() {
            @Override
            public ResourceLocation type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec(String name) {
                return codec.apply(name);
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }

}
