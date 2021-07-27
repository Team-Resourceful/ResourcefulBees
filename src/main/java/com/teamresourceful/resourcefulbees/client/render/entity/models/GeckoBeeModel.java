package com.teamresourceful.resourcefulbees.client.render.entity.models;

import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoBeeModel<E extends CustomBeeEntity> extends AnimatedGeoModel<E> {

    @Override
    public ResourceLocation getModelLocation(E object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/jack.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(E object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/item/jack.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(E animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/jackinthebox.animation.json");
    }
}
