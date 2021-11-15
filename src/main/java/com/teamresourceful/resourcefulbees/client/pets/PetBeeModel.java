package com.teamresourceful.resourcefulbees.client.pets;

import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PetBeeModel<T extends PetModelData> extends AnimatedGeoModel<T> {

    public GeoModel getModel(PetModelData object){
        return this.getModel(getModelLocation(object));
    }

    @Override
    public ResourceLocation getModelLocation(PetModelData object) {
        return object.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureLocation(PetModelData object) {
        return object.getTexture();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(PetModelData animatable) {
        return RenderData.BASE_ANIMATION;
    }
}
