package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record CodecIngredientSerializer<T extends CodecIngredient<T>>(ResourceLocation id, Codec<T> codec, Codec<T> network) {

    public CodecIngredientSerializer(ResourceLocation id, Codec<T> codec) {
        this(id, codec, codec);
    }
}
