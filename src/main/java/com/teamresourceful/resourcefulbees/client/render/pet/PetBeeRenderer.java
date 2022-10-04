package com.teamresourceful.resourcefulbees.client.render.pet;

import com.teamresourceful.resourcefulbees.client.pets.PetBeeModel;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PetBeeRenderer implements IGeoRenderer<PetModelData> {

    @Override
    public void setCurrentRTB(MultiBufferSource rtb) {
        //TODO figure out what goes here!
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        //TODO figure out what goes here!
        return null;
    }

    @Override
    public PetBeeModel<PetModelData> getGeoModelProvider() {
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(PetModelData instance) {
        return instance.getTexture();
    }

    @Override
    public Integer getUniqueID(PetModelData animatable) {
        return animatable.getId().hashCode();
    }
}
