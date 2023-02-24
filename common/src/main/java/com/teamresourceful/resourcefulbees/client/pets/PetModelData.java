package com.teamresourceful.resourcefulbees.client.pets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerTexture;
import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PetModelData implements IAnimatable {

    private static final ResourceLocation BASE_MODEL = new ModResourceLocation("geo/base.geo.json");

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").orElse(-1).forGetter(PetModelData::getVersion),
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            ResourceLocation.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(PetModelData::getModelLocation),
            ResourceLocation.CODEC.fieldOf("texture").orElse(LayerTexture.MISSING_TEXTURE.texture()).forGetter(PetModelData::getTexture),
            Codec.STRING.optionalFieldOf("asset").forGetter(data -> Optional.ofNullable(data.getUrlTexture())),
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    static {
        //noinspection unchecked
        AnimationController.addModelFetcher((IAnimatable object) -> object instanceof PetModelData data ? data.getModelRaw() : null);
    }

    private final PetBeeModel<PetModelData> model = new PetBeeModel<>();
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

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
        this.urlTexture = new PetTexture(id, urlTexture.orElse(null));
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
        ResourceLocation urlTexture = this.urlTexture.getResourceLocation();
        if (urlTexture != null) return urlTexture;
        return texture;
    }

    public String getUrlTexture() {
        return urlTexture.getTexture();
    }

    public Set<BeeLayerData> getLayers() {
        return layers;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bee.fly", ILoopType.EDefaultLoopTypes.LOOP).addAnimation("animation.bee.fly.bobbing", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
