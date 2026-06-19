package com.teamresourceful.resourcefulbees.client.rendering.entities.models;

import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.geckolib.model.GeoModel;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CustomBeeModel<E extends CustomBeeEntity> extends GeoModel<E> {

/*    @Override
    public Identifier getModelResource(E bee) {
        return bee.getRenderData().model();
    }

    @Override
    public Identifier getTextureResource(E bee) {
        return bee.getRenderData().texture().getTexture(bee);
    }*/

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return null;
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return null;
    }

    @Override
    public @NonNull Identifier getAnimationResource(E bee) {
        return bee.getRenderData().animations();
    }
}
