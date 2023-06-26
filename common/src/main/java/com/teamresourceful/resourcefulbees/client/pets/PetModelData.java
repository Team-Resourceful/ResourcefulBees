package com.teamresourceful.resourcefulbees.client.pets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerTexture;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PetModelData implements GeoAnimatable {

    private static final ResourceLocation BASE_MODEL = new ModResourceLocation("geo/base.geo.json");
    private static final RawAnimation ANIMATION = RawAnimation.begin().thenLoop("animation.bee.fly").thenLoop("animation.bee.fly.bobbing");

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").orElse(-1).forGetter(PetModelData::getVersion),
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            ResourceLocation.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(PetModelData::getModelLocation),
            ResourceLocation.CODEC.fieldOf("texture").orElse(LayerTexture.MISSING_TEXTURE.texture()).forGetter(PetModelData::getTexture),
            Codec.STRING.optionalFieldOf("asset").forGetter(data -> Optional.ofNullable(data.getUrlTexture())),
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    private final PetBeeModel<PetModelData> model = new PetBeeModel<>();
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final int version;
    private final String id;
    private final ResourceLocation modelLocation;
    private final ResourceLocation texture;
    private final PetTexture urlTexture;
    private final Set<BeeLayerData> layers;

    public PetModelData(int version, String id, ResourceLocation modelLocation, ResourceLocation texture, Optional<String> urlTexture, Set<BeeLayerData> layers) {
        this.version = version;
        this.id = id;
        this.modelLocation = modelLocation;
        this.texture = texture;
        this.urlTexture = urlTexture.map(url -> new PetTexture(id, url)).orElse(null);
        this.layers = layers;
    }

    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public PetBeeModel<PetModelData> getModel() {
        return model;
    }

    @SuppressWarnings("rawtypes")
    public PetBeeModel getModelRaw() {
        return model;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public ResourceLocation getTexture() {
        if (this.urlTexture != null) return this.urlTexture.getResourceLocation();
        return texture;
    }

    public String getUrlTexture() {
        return urlTexture == null ? null : urlTexture.getTexture();
    }

    public Set<BeeLayerData> getLayers() {
        return layers;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(ANIMATION);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }
}
