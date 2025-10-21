package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CentrifugeModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public CentrifugeModel() {
        super(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "centrifuge"));
    }

    @Override
    public ResourceLocation getModelResource(GeoAnimatable object) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "geo/blocks/centrifuge.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "textures/block/create_centrifuge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return ResourceLocation.fromNamespaceAndPath("resourcefulbees", "animations/centrifuge.animation.json");
    }
}
