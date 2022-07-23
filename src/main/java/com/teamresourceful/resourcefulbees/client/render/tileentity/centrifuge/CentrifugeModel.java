package com.teamresourceful.resourcefulbees.client.render.tileentity.centrifuge;

import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CentrifugeModel extends AnimatedGeoModel<CentrifugeBlockEntity> {

    @Override
    public ResourceLocation getModelResource(CentrifugeBlockEntity object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CentrifugeBlockEntity object) {
        return new ResourceLocation("resourcefulbees", "textures/block/create_centrifuge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CentrifugeBlockEntity animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge.animation.json");
    }
}
