package com.teamresourceful.resourcefulbees.client.render.entities.models;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CustomBeeModel<E extends CustomBeeEntity> extends AnimatedGeoModel<E> {

    @Override
    public ResourceLocation getModelResource(E bee) {
        return bee.getRenderData().model();
    }

    @Override
    public ResourceLocation getTextureResource(E bee) {
        return bee.getRenderData().texture().getTexture(bee);
    }

    @Override
    public ResourceLocation getAnimationResource(E bee) {
        return bee.getRenderData().animations();
    }
}
