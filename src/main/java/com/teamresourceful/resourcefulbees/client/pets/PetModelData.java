package com.teamresourceful.resourcefulbees.client.pets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.render.BeeTexture;
import com.teamresourceful.resourcefulbees.api.beedata.render.LayerData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class PetModelData implements IAnimatable {

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            ResourceLocation.CODEC.fieldOf("model").orElse(RenderData.BASE_MODEL).forGetter(PetModelData::getModelLocation),
            ResourceLocation.CODEC.fieldOf("texture").orElse(BeeTexture.MISSING_TEXTURE.normalTexture()).forGetter(PetModelData::getTexture),
            CodecUtils.createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    static {
        //noinspection unchecked
        AnimationController.addModelFetcher((IAnimatable object) -> object instanceof PetModelData ? ((PetModelData) object).getModel() : null);
    }

    private final PetBeeModel<PetModelData> model = new PetBeeModel<>();
    private final AnimationFactory factory = new AnimationFactory(this);

    private final String id;
    private final ResourceLocation modelLocation;
    private final ResourceLocation texture;
    private final Set<LayerData> layers;

    public PetModelData(String id, ResourceLocation modelLocation, ResourceLocation texture, Set<LayerData> layers) {
        this.id = id;
        this.modelLocation = modelLocation;
        this.texture = texture;
        this.layers = layers;
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("rawtypes")
    public PetBeeModel getModel() {
        return model;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public Set<LayerData> getLayers() {
        return layers;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bee.fly", true).addAnimation("animation.bee.fly.bobbing", true));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
