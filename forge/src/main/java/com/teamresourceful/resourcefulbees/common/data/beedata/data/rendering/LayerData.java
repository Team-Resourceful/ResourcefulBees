package com.teamresourceful.resourcefulbees.common.data.beedata.data.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefullib.common.color.Color;

public record LayerData(Color color, BeeLayerTexture texture, LayerEffect effect, boolean pollenLayer, float pulseFrequency) implements BeeLayerData {

    public static final BeeLayerData DEFAULT = new LayerData(Color.DEFAULT, LayerTexture.MISSING_TEXTURE, LayerEffect.NONE, false, 0);

    public static final Codec<BeeLayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(BeeLayerData::color),
            LayerTexture.CODEC.fieldOf("texture").orElse(LayerTexture.MISSING_TEXTURE).forGetter(BeeLayerData::texture),
            LayerEffect.CODEC.fieldOf("layerEffect").orElse(LayerEffect.NONE).forGetter(BeeLayerData::effect),
            Codec.BOOL.fieldOf("isPollen").orElse(false).forGetter(BeeLayerData::pollenLayer),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(BeeLayerData::pulseFrequency)
    ).apply(instance, LayerData::new));

}
