package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.utils.color.Color;

public class ColorData {

    public static final Codec<ColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("spawnEggPrimaryColor").orElse(Color.WHITE).forGetter(ColorData::getJarColor),
            Color.CODEC.fieldOf("spawnEggSecondaryColor").orElse(Color.WHITE).forGetter(ColorData::getJarColor),
            Color.CODEC.fieldOf("jarColor").orElse(Color.WHITE).forGetter(ColorData::getJarColor)
    ).apply(instance, ColorData::new));

    public static final ColorData DEFAULT = new ColorData(Color.WHITE, Color.WHITE, Color.WHITE);

    private final Color spawnEggPrimaryColor;
    private final Color spawnEggSecondaryColor;
    private final Color jarColor;

    private ColorData(Color spawnEggPrimaryColor, Color spawnEggSecondaryColor, Color jarColor) {
        this.spawnEggPrimaryColor = spawnEggPrimaryColor;
        this.spawnEggSecondaryColor = spawnEggSecondaryColor;
        this.jarColor = jarColor;
    }

    public Color getSpawnEggPrimaryColor() {
        return spawnEggPrimaryColor;
    }

    public Color getSpawnEggSecondaryColor() {
        return spawnEggSecondaryColor;
    }

    public Color getJarColor() {
        return jarColor;
    }
}
