package com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CentrifugeModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public CentrifugeModel() {
        super(new ResourceLocation(ModConstants.MOD_ID, "centrifuge"));
    }

    @Override
    public ResourceLocation getModelResource(GeoAnimatable object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation("resourcefulbees", "textures/block/create_centrifuge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge.animation.json");
    }
}
