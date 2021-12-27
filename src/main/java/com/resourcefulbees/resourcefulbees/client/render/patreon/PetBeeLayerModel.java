package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.resourcefulbees.resourcefulbees.client.render.entity.GeckoBeeModel;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PetBeeLayerModel<T extends PetData> extends AnimatedGeoModel<T> {

    public GeoModel getModel(PetData object){
        return this.getModel(getModelLocation(object));
    }

    @Override
    public ResourceLocation getModelLocation(PetData object) {
        return object.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureLocation(PetData object) {
        return object.getTexture();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(PetData animatable) {
        return GeckoBeeModel.BASE_ANIMATION;
    }
}
