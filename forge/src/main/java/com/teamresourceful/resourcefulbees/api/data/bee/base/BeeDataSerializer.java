package com.teamresourceful.resourcefulbees.api.data.bee.base;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface BeeDataSerializer<T extends BeeData<T>> {

    default ResourceLocation id() {
        return new ResourceLocation(type().getNamespace(), type().getPath() + "/v" + version());
    }

    ResourceLocation type();

    int version();

    Codec<T> codec(String id);

    default @Nullable T defaultValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default T cast(BeeData<?> data) {
        return (T) data;
    }

    static <T extends BeeData<T>> BeeDataSerializer<T> of(ResourceLocation id, int version, Function<String, Codec<T>> codec, @Nullable T defaultValue) {
        return new BeeDataSerializer<>() {
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
