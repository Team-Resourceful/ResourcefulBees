package com.resourcefulbees.resourcefulbees.client.render.patreon;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class PetData implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    private final ResourceLocation modelLocation;
    private final ResourceLocation texture;

    static {
        //noinspection unchecked
        AnimationController.addModelFetcher((IAnimatable object) -> object instanceof PetData ? ((PetData) object).getModel() : null);
    }

    private final PetBeeModel<PetData> model = new PetBeeModel<>();

    public PetData(ResourceLocation modelLocation, ResourceLocation texture) {
        this.modelLocation = modelLocation;
        this.texture = texture;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bee.fly", true).addAnimation("animation.bee.fly.bobbing", true));
            return PlayState.CONTINUE;
        }));
    }

    @SuppressWarnings("rawtypes")
    public PetBeeModel getModel() {
        return model;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
