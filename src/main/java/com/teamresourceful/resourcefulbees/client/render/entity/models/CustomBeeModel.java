package com.teamresourceful.resourcefulbees.client.render.entity.models;

import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
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
