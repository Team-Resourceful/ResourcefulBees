package com.teamresourceful.resourcefulbees.api.data.bee.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface BeeDataSerializer<T extends BeeData<T>> {

    default Identifier id() {
        return Identifier.fromNamespaceAndPath(type().getNamespace(), type().getPath() + "/v" + version());
    }

    Identifier type();

    int version();

    Codec<T> codec(String id);

    default @Nullable T defaultValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default T cast(BeeData<?> data) {
        return (T) data;
    }

    static <T extends BeeData<T>> BeeDataSerializer<T> of(Identifier id, int version, Function<String, MapCodec<T>> codec, @Nullable T defaultValue) {
        return new BeeDataSerializer<>() {
            @Override
            public Identifier type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec(String name) {
                return codec.apply(name).codec();
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }
}
