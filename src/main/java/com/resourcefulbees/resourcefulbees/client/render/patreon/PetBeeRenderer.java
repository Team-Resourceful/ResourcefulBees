package com.resourcefulbees.resourcefulbees.client.render.patreon;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PetBeeRenderer implements IGeoRenderer<PetModelData> {

    private IRenderTypeBuffer rtb;

    @Override
    public PetBeeModel getGeoModelProvider() {
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

    public void setCurrentRTB(IRenderTypeBuffer rtb) {
        this.rtb = rtb;
    }

    public IRenderTypeBuffer getCurrentRTB() {
        return rtb;
    }
}
