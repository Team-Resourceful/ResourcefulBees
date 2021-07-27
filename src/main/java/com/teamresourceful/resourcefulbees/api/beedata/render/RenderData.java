package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.lib.enums.BaseModelType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

//This class does not need a mutable version as mod devs should supply a json version
//of the bee if they wish for us to register the entity type.
@Unmodifiable
public class RenderData {
    public static final RenderData DEFAULT = new RenderData(Collections.singleton(LayerData.DEFAULT), ColorData.DEFAULT, BaseModelType.DEFAULT, 1.0f);

    /**
     * A {@link Codec<RenderData>} that can be parsed to create a {@link RenderData} object.
     */
    public static final Codec<RenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(CodecUtils.newLinkedHashSet(LayerData.DEFAULT)).forGetter(RenderData::getLayers),
            ColorData.CODEC.fieldOf("ColorData").orElse(ColorData.DEFAULT).forGetter(RenderData::getColorData),
            BaseModelType.CODEC.fieldOf("baseModelType").orElse(BaseModelType.DEFAULT).forGetter(RenderData::getBaseModelType),
            Codec.floatRange(0.5f, 2.0f).fieldOf("sizeModifier").orElse(1.0f).forGetter(RenderData::getSizeModifier)
    ).apply(instance, RenderData::new));

    private final Set<LayerData> layers;
    private final ColorData colorData;
    private final float sizeModifier;
    private final BaseModelType baseModelType;

    private RenderData(Set<LayerData> layers, ColorData colorData, BaseModelType baseModelType, float sizeModifier) {
        this.layers = layers;
        this.baseModelType = baseModelType;
        this.colorData = colorData;
        this.sizeModifier = sizeModifier;
    }

    /**
     * This method returns an unmodifiable {@link Set} of {@link LayerData}
     * that is backed by a {@link LinkedHashSet} so the layers are added to
     * the bee properly.
     *
     * @return Returns an unmodifiable {@link Set} of {@link LayerData}.
     */
    public Set<LayerData> getLayers() {
        return Collections.unmodifiableSet(layers);
    }

    /**
     * Gets a {@link ColorData} object that contains the color data for the bee's
     * spawn egg and jar color.
     *
     * @return Returns a {@link ColorData} object.
     */
    public ColorData getColorData() {
        return colorData;
    }

    /**
     * Gets the {@link BaseModelType} for the associated bee. This model type has
     * only two options:
     * <br>
     * <b>DEFAULT</b> - The normal vanilla style model
     * <br>
     * <b>KITTEN</b> - A kitten-like model that can be used with any other model
     *
     * @return Returns the {@link BaseModelType} for the associated bee.
     */
    public BaseModelType getBaseModelType() { return baseModelType; }

    /**
     * Gets the size modifier for the bee. The bee is scaled by this value
     * with a range of 0.5 -> 2.0, where a value of 0.5 would make the bee
     * <b>half</b> the size of a vanilla bee and a value of 2.0 would make the bee
     * <b>twice</b> the size of a vanilla bee.
     *
     * @return Returns the size modifier for the bee as a {@link Float}
     */
    public float getSizeModifier() {
        return sizeModifier;
    }
}
