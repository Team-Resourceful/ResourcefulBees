package com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefullib.common.color.Color;

public record ColorData(Color spawnEgg, Color jarColor) implements BeeColorData {

    public static final Codec<BeeColorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.optionalFieldOf("spawnEgg", Color.DEFAULT).forGetter(BeeColorData::spawnEgg),
            Color.CODEC.optionalFieldOf("jarColor", Color.DEFAULT).forGetter(BeeColorData::jarColor)
    ).apply(instance, ColorData::new));

    public static final BeeColorData DEFAULT = new ColorData(Color.DEFAULT, Color.DEFAULT);

}
