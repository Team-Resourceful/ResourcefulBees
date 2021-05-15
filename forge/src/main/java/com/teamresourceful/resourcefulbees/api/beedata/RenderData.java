package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.lib.BaseModelTypes;
import com.teamresourceful.resourcefulbees.lib.ModelTypes;

import java.util.Collections;
import java.util.Set;

public class RenderData {

    public static final Codec<RenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(CodecUtils.newLinkedHashSet(LayerData.DEFAULT)).forGetter(RenderData::getLayers),
            ColorData.CODEC.fieldOf("ColorData").orElse(ColorData.DEFAULT).forGetter(RenderData::getColorData),
            ModelTypes.CODEC.fieldOf("modelType").orElse(ModelTypes.DEFAULT).forGetter(RenderData::getModelType),
            BaseModelTypes.CODEC.fieldOf("baseModelType").orElse(BaseModelTypes.DEFAULT).forGetter(RenderData::getBaseModelType),
            Codec.floatRange(0.5f, 2.0f).fieldOf("sizeModifier").orElse(1.0f).forGetter(RenderData::getSizeModifier)
    ).apply(instance, RenderData::new));


    public static final RenderData DEFAULT = new RenderData(Collections.singleton(LayerData.DEFAULT), ColorData.DEFAULT, ModelTypes.DEFAULT, BaseModelTypes.DEFAULT, 1.0f);

    private final Set<LayerData> layers;
    private final ColorData colorData;
    private final ModelTypes modelType;
    private final float sizeModifier;

    /**
     * The base model the bee uses
     */
    private final BaseModelTypes baseModelType;

    private RenderData(Set<LayerData> layers, ColorData colorData, ModelTypes modelType, BaseModelTypes baseModelType, float sizeModifier) {
        this.layers = layers;
        this.modelType = modelType;
        this.baseModelType = baseModelType;
        this.colorData = colorData;
        this.sizeModifier = sizeModifier;
    }

    public Set<LayerData> getLayers() {
        return Collections.unmodifiableSet(layers);
    }

    public ColorData getColorData() {
        return colorData;
    }

    public ModelTypes getModelType() { return modelType; }

    public BaseModelTypes getBaseModelType() { return baseModelType; }

    public float getSizeModifier() {
        return sizeModifier;
    }
}
