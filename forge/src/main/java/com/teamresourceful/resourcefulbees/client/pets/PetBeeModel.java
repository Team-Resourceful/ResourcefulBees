package com.teamresourceful.resourcefulbees.client.pets;

import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PetBeeModel<T extends PetModelData> extends AnimatedGeoModel<T> {

    private static final ResourceLocation ANIMATION = new ModResourceLocation( "animations/bee.animation.json");

    public GeoModel getModel(PetModelData object){
        return this.getModel(getModelResource(object));
    }

    @Override
    public ResourceLocation getModelResource(PetModelData object) {
        return object.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(PetModelData object) {
        return object.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(PetModelData animatable) {
        return ANIMATION;
    }
}
