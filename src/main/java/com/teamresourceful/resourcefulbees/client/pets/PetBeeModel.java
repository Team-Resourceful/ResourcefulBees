package com.teamresourceful.resourcefulbees.client.pets;

import com.teamresourceful.resourcefulbees.common.util.ModResourceLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class PetBeeModel<T extends PetModelData> extends GeoModel<T> {

    private static final ResourceLocation ANIMATION = new ModResourceLocation( "animations/bee.animation.json");

    public BakedGeoModel getModel(PetModelData object){
        return this.getBakedModel(getModelResource(object));
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
