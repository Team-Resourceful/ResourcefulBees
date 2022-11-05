package com.teamresourceful.resourcefulbees.api.moddata;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface ModDataSerializer<T extends ModData<T>> {

    ResourceLocation id();

    Codec<T> codec();

    static <T extends ModData<T>> ModDataSerializer<T> of(ResourceLocation id, Codec<T> codec) {
        return new ModDataSerializer<>() {
            @Override
            public ResourceLocation id() {
                return id;
            }

            @Override
            public Codec<T> codec() {
                return codec;
            }
        };
    }
}
