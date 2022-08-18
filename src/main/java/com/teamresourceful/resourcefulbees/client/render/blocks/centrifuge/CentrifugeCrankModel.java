package com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.blockentity.centrifuge.CentrifugeCrankBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CentrifugeCrankModel extends AnimatedGeoModel<CentrifugeCrankBlockEntity> {

    @Override
    public ResourceLocation getModelResource(CentrifugeCrankBlockEntity object) {
        return new ResourceLocation("resourcefulbees", "geo/blocks/centrifuge_crank.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CentrifugeCrankBlockEntity object) {
        return new ResourceLocation("resourcefulbees", "textures/block/centrifuge_crank.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CentrifugeCrankBlockEntity animatable) {
        return new ResourceLocation("resourcefulbees", "animations/centrifuge_crank.animation.json");
    }
}
