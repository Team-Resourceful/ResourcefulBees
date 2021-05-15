package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.utils.color.Color;

public class ColorData {

    public static final Codec<ColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("spawnEggPrimaryColor").orElse(Color.DEFAULT).forGetter(ColorData::getSpawnEggPrimaryColor),
            Color.CODEC.fieldOf("spawnEggSecondaryColor").orElse(Color.DEFAULT).forGetter(ColorData::getSpawnEggSecondaryColor),
            Color.CODEC.fieldOf("jarColor").orElse(Color.DEFAULT).forGetter(ColorData::getJarColor)
    ).apply(instance, ColorData::new));

    public static final ColorData DEFAULT = new ColorData(Color.DEFAULT, Color.DEFAULT, Color.DEFAULT);

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
