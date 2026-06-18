package com.teamresourceful.resourcefulbees.client.pets;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.LayerTexture;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PetModelData implements GeoAnimatable {

    private static final Identifier BASE_MODEL = ModIdentifier.of("geo/base.geo.json");
    private static final RawAnimation ANIMATION = RawAnimation.begin().thenLoop("animation.bee.fly").thenLoop("animation.bee.fly.bobbing");

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").orElse(-1).forGetter(PetModelData::getVersion),
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            Identifier.CODEC.fieldOf("model").orElse(BASE_MODEL).forGetter(PetModelData::getModelLocation),
            Identifier.CODEC.fieldOf("texture").orElse(LayerTexture.MISSING_TEXTURE.texture()).forGetter(PetModelData::getTexture),
            Codec.STRING.optionalFieldOf("asset").forGetter(data -> Optional.ofNullable(data.getUrlTexture())),
            CodecExtras.linkedSet(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    private final PetBeeModel<PetModelData> model = new PetBeeModel<>();
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private final int version;
    private final String id;
    private final Identifier modelLocation;
    private final Identifier texture;
    private final PetTexture urlTexture;
    private final Set<BeeLayerData> layers;

    public PetModelData(int version, String id, Identifier modelLocation, Identifier texture, Optional<String> urlTexture, Set<BeeLayerData> layers) {
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

    public Identifier getModelLocation() {
        return modelLocation;
    }

    public Identifier getTexture() {
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
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
        /*controllers.add(new AnimationController<>("bee_controller", 0, new AnimationController<>(ANIMATION)));

        controllers.add(new AnimationController<>(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(ANIMATION);
            return PlayState.CONTINUE;
        })));*/
    }

    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

/*    @Override
    public double getTick(Object o) {
        return RenderUtil.getCurrentTick();
    }*/
}
