package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;

public record ColorData(Color spawnEggPrimaryColor, Color spawnEggSecondaryColor, Color jarColor) {

    public static final Codec<ColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("spawnEggPrimaryColor").orElse(Color.DEFAULT).forGetter(ColorData::spawnEggPrimaryColor),
            Color.CODEC.fieldOf("spawnEggSecondaryColor").orElse(Color.DEFAULT).forGetter(ColorData::spawnEggSecondaryColor),
            Color.CODEC.fieldOf("jarColor").orElse(Color.DEFAULT).forGetter(ColorData::jarColor)
    ).apply(instance, ColorData::new));

    public static final ColorData DEFAULT = new ColorData(Color.DEFAULT, Color.DEFAULT, Color.DEFAULT);
}
