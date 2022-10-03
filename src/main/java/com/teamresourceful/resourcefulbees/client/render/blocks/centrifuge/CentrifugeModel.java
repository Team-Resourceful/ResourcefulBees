package com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CentrifugeModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation getModelResource(IAnimatable object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IAnimatable object) {
        return new ResourceLocation("resourcefulbees", "textures/block/create_centrifuge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IAnimatable animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge.animation.json");
    }
}
