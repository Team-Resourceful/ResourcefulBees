package com.teamresourceful.resourcefulbees.client.render.pet;

import com.teamresourceful.resourcefulbees.client.pets.PetBeeModel;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PetBeeRenderer implements IGeoRenderer<PetModelData> {

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
}
