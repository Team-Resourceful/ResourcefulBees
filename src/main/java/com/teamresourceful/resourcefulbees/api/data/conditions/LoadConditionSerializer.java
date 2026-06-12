package com.teamresourceful.resourcefulbees.api.data.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public interface LoadConditionSerializer<T extends LoadCondition<T>> {

    Identifier id();

    Codec<T> codec();

    static <T extends LoadCondition<T>> LoadConditionSerializer<T> of(Identifier id, Codec<T> codec) {
        return new LoadConditionSerializer<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Codec<T> codec() {
                return codec;
            }
        };
    }
}
