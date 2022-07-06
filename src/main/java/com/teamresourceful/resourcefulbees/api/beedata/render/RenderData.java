package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @param layers Returns an unmodifiable {@link Set} of {@link LayerData}.s
 */
public record RenderData(Set<LayerData> layers, ColorData colorData, ResourceLocation model, BeeTexture texture, ResourceLocation animations, float sizeModifier) {

    public static final ResourceLocation BASE_MODEL = new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base.geo.json");
    public static final ResourceLocation BASE_ANIMATION = new ResourceLocation(ResourcefulBees.MOD_ID, "animations/bee.animation.json");

    public static final RenderData DEFAULT = new RenderData(Collections.emptySet(), ColorData.DEFAULT, BASE_MODEL, BeeTexture.MISSING_TEXTURE, BASE_ANIMATION, 1.0f);

    /**
     * A {@link Codec<RenderData>} that can be parsed to create a {@link RenderData} object.
     */
    public static final Codec<RenderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new HashSet<>()).forGetter(RenderData::layers),
            ColorData.CODEC.fieldOf("ColorData").orElse(ColorData.DEFAULT).forGetter(RenderData::colorData),
            ResourceLocation.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(RenderData::model),
            BeeTexture.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE).forGetter(RenderData::texture),
            ResourceLocation.CODEC.fieldOf("animation").orElse(BASE_ANIMATION).forGetter(RenderData::animations),
            Codec.floatRange(0.5f, 2.0f).fieldOf("sizeModifier").orElse(1.0f).forGetter(RenderData::sizeModifier)
    ).apply(instance, RenderData::new));

    public RenderData(Set<LayerData> layers, ColorData colorData, ResourceLocation model, BeeTexture texture, ResourceLocation animations, float sizeModifier) {
        this.layers = Collections.unmodifiableSet(layers);
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
    @Override
    public Set<LayerData> layers() {
        return layers;
    }

    /**
     * Gets the size modifier for the bee. The bee is scaled by this value
     * with a range of 0.5 -> 2.0, where a value of 0.5 would make the bee
     * <b>half</b> the size of a vanilla bee and a value of 2.0 would make the bee
     * <b>twice</b> the size of a vanilla bee.
     *
     * @return Returns the size modifier for the bee as a {@link Float}
     */
    @Override
    public float sizeModifier() {
        return sizeModifier;
    }
}
