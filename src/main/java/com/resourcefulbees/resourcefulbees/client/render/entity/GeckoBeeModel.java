package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoBeeModel<E extends CustomBeeEntity> extends AnimatedGeoModel<E> {

    public static final ResourceLocation BASE_ANIMATION = new ResourceLocation(ResourcefulBees.MOD_ID, "animations/bee.animation.json");

    @Override
    public ResourceLocation getModelLocation(E bee) {
        return bee.getBeeData().getColorData().getModeLocation();
    }

    @Override
    public ResourceLocation getTextureLocation(E bee) {
        ResourceLocation layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + bee.getBeeData().getBaseLayerTexture() + ".png");
        ResourceLocation angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + bee.getBeeData().getBaseLayerTexture() + "_angry.png");
        return bee.isAngry() ? angerLayerTexture : layerTexture;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(E bee) {
        return BASE_ANIMATION;
    }
}
