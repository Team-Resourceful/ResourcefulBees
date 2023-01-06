package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface CodecIngredientSerializer<T extends CodecIngredient<T>> {

    ResourceLocation id();

    Codec<T> codec();

    default Codec<T> network() {
        return codec();
    }
}
