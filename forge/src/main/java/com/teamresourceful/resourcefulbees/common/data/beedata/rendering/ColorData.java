package com.teamresourceful.resourcefulbees.common.data.beedata.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefullib.common.color.Color;

public record ColorData(Color primarySpawnEggColor, Color secondarySpawnEggColor, Color jarColor) implements BeeColorData {

    public static final Codec<BeeColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("spawnEggPrimaryColor").orElse(Color.DEFAULT).forGetter(BeeColorData::primarySpawnEggColor),
            Color.CODEC.fieldOf("spawnEggSecondaryColor").orElse(Color.DEFAULT).forGetter(BeeColorData::secondarySpawnEggColor),
            Color.CODEC.fieldOf("jarColor").orElse(Color.DEFAULT).forGetter(BeeColorData::jarColor)
    ).apply(instance, ColorData::new));

    public static final BeeColorData DEFAULT = new ColorData(Color.DEFAULT, Color.DEFAULT, Color.DEFAULT);

}
