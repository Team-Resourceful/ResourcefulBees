package com.teamresourceful.resourcefulbees.api.data.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface LoadConditionSerializer<T extends LoadCondition<T>> {

    ResourceLocation id();

    Codec<T> codec();

    static <T extends LoadCondition<T>> LoadConditionSerializer<T> of(ResourceLocation id, Codec<T> codec) {
        return new LoadConditionSerializer<>() {
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
