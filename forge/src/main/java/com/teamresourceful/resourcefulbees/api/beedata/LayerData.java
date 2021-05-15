package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.utils.color.Color;

public class LayerData {

    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(LayerData::getColor),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(LayerData::getBeeTexture),
            Codec.BOOL.fieldOf("isGlowing").orElse(false).forGetter(LayerData::isEmissive),
            Codec.BOOL.fieldOf("isEnchanted").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(LayerData::getPulseFrequency)
    ).apply(instance, LayerData::new));

    public static final LayerData DEFAULT = new LayerData(Color.DEFAULT, BeeTexture.MISSING_TEXTURE, false, false, 0);

    private final Color color;
    private final BeeTexture beeTexture;
    private final boolean isEmissive;
    private final boolean isEnchanted;
    private final float pulseFrequency;

    public LayerData(Color color, BeeTexture beeTexture, boolean isEmissive, boolean isEnchanted, float pulseFrequency) {
        this.color = color;
        this.beeTexture = beeTexture;
        this.isEmissive = isEmissive;
        this.isEnchanted = isEnchanted;
        this.pulseFrequency = pulseFrequency;
    }

    public Color getColor() {
        return color;
    }

    public BeeTexture getBeeTexture() {
        return beeTexture;
    }

    public boolean isEmissive() {
        return isEmissive && !isEnchanted;
    }

    public boolean isEnchanted() {
        return isEnchanted;
    }

    public float getPulseFrequency() {
        return pulseFrequency;
    }
}
