package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugeCrankModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(GeoAnimatable object) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "geo/blocks/centrifuge_crank.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable object) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "textures/block/centrifuge_crank.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "animations/centrifuge_crank.animation.json");
    }
}
