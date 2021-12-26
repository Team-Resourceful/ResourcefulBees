package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;

public class LayerData {

    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("color").orElse("-1").forGetter(LayerData::getColor),
            ResourceLocation.CODEC.fieldOf("texture").orElse(new ResourceLocation("textures/entity/bee/bee.png")).forGetter(LayerData::getBeeTexture),
            Codec.BOOL.fieldOf("isGlowing").orElse(false).forGetter(LayerData::isEmissive),
            Codec.BOOL.fieldOf("isEnchanted").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.BOOL.fieldOf("isPollen").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(LayerData::getPulseFrequency)
    ).apply(instance, LayerData::new));

    private final String color;
    private final ResourceLocation beeTexture;
    private final boolean isEmissive;
    private final boolean isEnchanted;
    private final float pulseFrequency;
    private final boolean isPollen;

    private LayerData(String color, ResourceLocation beeTexture, boolean isEmissive, boolean isEnchanted, boolean isPollen, float pulseFrequency) {
        this.color = color;
        this.beeTexture = beeTexture;
        this.isEmissive = isEmissive;
        this.isEnchanted = isEnchanted;
        this.pulseFrequency = pulseFrequency;
        this.isPollen = isPollen;
    }

    public String getColor() {
        return color;
    }

    public ResourceLocation getBeeTexture() {
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

    public boolean isPollen() {
        return isPollen;
    }
}
