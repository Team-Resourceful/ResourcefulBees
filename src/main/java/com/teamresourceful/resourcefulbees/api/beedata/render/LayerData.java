package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;

/**
 * @param pulseFrequency the ticks between pulses for glowing effect.
 * @param beeTexture the texture in which the layer uses.
 */
public record LayerData(Color color, BeeTexture beeTexture, LayerEffect effect,  boolean isPollen, float pulseFrequency) {
    public static final LayerData DEFAULT = new LayerData(Color.DEFAULT, BeeTexture.MISSING_TEXTURE, LayerEffect.NONE, false, 0);

    /**
     * A {@link Codec<LayerData>} that can be parsed to create a {@link LayerData} object.
     */
    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(LayerData::color),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(LayerData::beeTexture),
            LayerEffect.CODEC.fieldOf("layerEffect").orElse(LayerEffect.NONE).forGetter(LayerData::effect),
            Codec.BOOL.fieldOf("isPollen").orElse(false).forGetter(LayerData::isPollen),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(LayerData::pulseFrequency)
    ).apply(instance, LayerData::new));
}
