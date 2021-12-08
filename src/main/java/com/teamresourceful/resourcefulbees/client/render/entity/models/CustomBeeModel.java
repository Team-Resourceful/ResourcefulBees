package com.teamresourceful.resourcefulbees.client.render.entity.models;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CustomBeeModel<E extends CustomBeeEntity> extends AnimatedGeoModel<E> {

    @Override
    public ResourceLocation getModelLocation(E bee) {
        return bee.getRenderData().getModel();
    }

    @Override
    public ResourceLocation getTextureLocation(E bee) {
        return bee.getRenderData().getTexture().getTexture(bee);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(E bee) {
        return bee.getRenderData().getAnimations();
    }
}
