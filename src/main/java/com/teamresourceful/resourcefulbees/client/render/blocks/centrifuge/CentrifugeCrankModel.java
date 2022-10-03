package com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CentrifugeCrankModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation getModelResource(IAnimatable object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge_crank.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IAnimatable object) {
        return new ResourceLocation("resourcefulbees", "textures/block/centrifuge_crank.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IAnimatable animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge_crank.animation.json");
    }
}
