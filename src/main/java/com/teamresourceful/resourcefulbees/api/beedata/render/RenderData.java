package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

//This class does not need a mutable version as mod devs should supply a json version
//of the bee if they wish for us to register the entity type.
@Unmodifiable
public class RenderData {
    public static final ResourceLocation BASE_MODEL = new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base.geo.json");
    public static final ResourceLocation BASE_ANIMATION = new ResourceLocation(ResourcefulBees.MOD_ID, "animations/bee.animation.json");

    public static final RenderData DEFAULT = new RenderData(Collections.emptySet(), ColorData.DEFAULT, BASE_MODEL, BeeTexture.MISSING_TEXTURE, BASE_ANIMATION, 1.0f);

    /**
     * A {@link Codec<RenderData>} that can be parsed to create a {@link RenderData} object.
     */
    public static final Codec<RenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(new HashSet<>()).forGetter(RenderData::getLayers),
            ColorData.CODEC.fieldOf("ColorData").orElse(ColorData.DEFAULT).forGetter(RenderData::getColorData),
            ResourceLocation.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(RenderData::getModel),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(RenderData::getTexture),
            ResourceLocation.CODEC.fieldOf("animation").orElse(BASE_ANIMATION).forGetter(RenderData::getModel),
            Codec.floatRange(0.5f, 2.0f).fieldOf("sizeModifier").orElse(1.0f).forGetter(RenderData::getSizeModifier)
    ).apply(instance, RenderData::new));

    private final Set<LayerData> layers;
    private final ColorData colorData;
    private final ResourceLocation model;
    private final BeeTexture texture;
    private final ResourceLocation animations;
    private final float sizeModifier;

    private RenderData(Set<LayerData> layers, ColorData colorData, ResourceLocation model, BeeTexture texture, ResourceLocation animations, float sizeModifier) {
        this.layers = layers;
        this.colorData = colorData;
        this.model = model;
        this.texture = texture;
        this.animations = animations;
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
     * @return Returns the {@link ResourceLocation} for the associated bee model.
     */
    public ResourceLocation getModel() { return model; }

    /**
     * @return Returns the {@link BeeTexture} for the associated bee.
     */
    public BeeTexture getTexture() { return texture; }

    /**
     * @return Returns the {@link ResourceLocation} for the associated bee animations.
     */
    public ResourceLocation getAnimations() { return animations; }

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
