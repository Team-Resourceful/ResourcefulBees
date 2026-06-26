package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CentrifugeCrankModel<T extends GeoAnimatable> extends GeoModel<T> {

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState object) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "geo/blocks/centrifuge_crank.geo.json");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState object) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "textures/block/centrifuge_crank.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull GeoAnimatable animatable) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "animation/centrifuge_crank.animation.json");
    }
}
