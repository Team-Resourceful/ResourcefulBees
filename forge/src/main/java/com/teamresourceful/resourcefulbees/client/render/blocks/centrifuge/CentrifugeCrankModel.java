package com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugeCrankModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(GeoAnimatable object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge_crank.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable object) {
        return new ResourceLocation("resourcefulbees", "textures/block/centrifuge_crank.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge_crank.animation.json");
    }
}
