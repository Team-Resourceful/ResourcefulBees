package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;

/**
 * @param pulseFrequency the ticks between pulses for glowing effect.
 * @param beeTexture the texture in which the layer uses.
 */
public record LayerData(Color color, BeeTexture beeTexture, boolean isEmissive, boolean isEnchanted, boolean isPollen, float pulseFrequency) {
    public static final LayerData DEFAULT = new LayerData(Color.DEFAULT, BeeTexture.MISSING_TEXTURE, false, false, false, 0);

    /**
     * A {@link Codec<LayerData>} that can be parsed to create a {@link LayerData} object.
     */
    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(LayerData::color),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(LayerData::beeTexture),
            Codec.BOOL.fieldOf("isGlowing").orElse(false).forGetter(LayerData::isEmissive),
            Codec.BOOL.fieldOf("isEnchanted").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.BOOL.fieldOf("isPollen").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(LayerData::pulseFrequency)
    ).apply(instance, LayerData::new));

    /**
     * Returns <b>true</b> if this layer is glowing.<br><i>Note that this
     * option is incompatible with the {@link LayerData#isEmissive}</i>
     * option due to conflicts in the renderer.
     *
     * @return Returns <b>true</b> if this layer is glowing.
     */
    @Override
    public boolean isEmissive() {
        return isEmissive && !isEnchanted;
    }
}
