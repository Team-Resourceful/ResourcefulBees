package com.teamresourceful.resourcefulbees.client.render.pet;

import com.teamresourceful.resourcefulbees.client.pets.PetBeeModel;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PetBeeRenderer implements IGeoRenderer<PetModelData> {

    private MultiBufferSource rtb;

    @Override
    public void setCurrentRTB(MultiBufferSource rtb) {
        this.rtb = rtb;
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        return rtb;
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
    public int getInstanceId(PetModelData animatable) {
        return animatable.getId().hashCode();
    }
}
