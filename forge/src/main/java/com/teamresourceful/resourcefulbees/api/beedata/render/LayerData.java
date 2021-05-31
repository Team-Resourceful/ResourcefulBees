package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.lib.enums.ModelType;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class LayerData {
    public static final LayerData DEFAULT = new LayerData(Color.DEFAULT, ModelType.DEFAULT,  BeeTexture.MISSING_TEXTURE, false, false, 0);

    /**
     * A {@link Codec<LayerData>} that can be parsed to create a {@link LayerData} object.
     */
    public static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(LayerData::getColor),
            ModelType.CODEC.fieldOf("modelType").orElse(ModelType.DEFAULT).forGetter(LayerData::getModelType),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(LayerData::getBeeTexture),
            Codec.BOOL.fieldOf("isGlowing").orElse(false).forGetter(LayerData::isEmissive),
            Codec.BOOL.fieldOf("isEnchanted").orElse(false).forGetter(LayerData::isEnchanted),
            Codec.floatRange(5f, 100f).fieldOf("pulseFrequency").orElse(0f).forGetter(LayerData::getPulseFrequency)
    ).apply(instance, LayerData::new));

    private final Color color;
    private final ModelType modelType;
    private final BeeTexture beeTexture;
    private final boolean isEmissive;
    private final boolean isEnchanted;
    private final float pulseFrequency;

    private LayerData(Color color, ModelType modelType, BeeTexture beeTexture, boolean isEmissive, boolean isEnchanted, float pulseFrequency) {
        this.color = color;
        this.modelType = modelType;
        this.beeTexture = beeTexture;
        this.isEmissive = isEmissive;
        this.isEnchanted = isEnchanted;
        this.pulseFrequency = pulseFrequency;
    }

    /**
     * Gets the color for this layer in the {@link CustomBeeRenderer}.
     * The color has a default value of white.
     *
     * @return Returns a {@link Color} object for this layer.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the {@link ModelType} for this layer in the {@link CustomBeeRenderer}
     * as a {@link CustomBeeModel}.
     * The default value is {@link ModelType#DEFAULT}.
     *
     * @return Returns the {@link ModelType} for this layer.
     */
    public ModelType getModelType() { return modelType; }

    /**
     * Gets the {@link BeeTexture} for this layer in the {@link CustomBeeRenderer}.
     * The default value is {@link BeeTexture#MISSING_TEXTURE}. This object contains
     * both the angry and normal variants.
     *
     * @return Returns a {@link BeeTexture} for this layer.
     */
    public BeeTexture getBeeTexture() {
        return beeTexture;
    }

    /**
     * Returns <b>true</b> if this layer is glowing.<br><i>Note that this
     * option is incompatible with the {@link LayerData#isEmissive}</i>
     * option due to conflicts in the renderer.
     *
     * @return Returns <b>true</b> if this layer is glowing.
     */
    public boolean isEmissive() {
        return isEmissive && !isEnchanted;
    }

    /**
     * Returns <b>true</b> if this layer has the Item Enchantment effect.
     * <br><i>Note that this option is incompatible with the {@link LayerData#isEmissive}</i>
     * option due to conflicts in the renderer.
     *
     * @return Returns <b>true</b> if this layer has the Item Enchantment effect.
     */
    public boolean isEnchanted() {
        return isEnchanted;
    }

    /**
     * The glowing effect can be pulsed using this value. The pulse frequency
     * is a {@link Float} indicating the number of ticks between pulses. The minimum
     * amount this value can be is <tt>5.0</tt> translating to approximately four times per second.
     *
     * @return Returns a {@link Float} indicating the number of ticks between pulses.
     */
    public float getPulseFrequency() {
        return pulseFrequency;
    }
}
