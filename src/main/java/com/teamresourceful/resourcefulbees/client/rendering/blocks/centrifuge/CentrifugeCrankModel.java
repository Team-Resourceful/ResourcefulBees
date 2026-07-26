package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CentrifugeCrankModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public CentrifugeCrankModel() {
        super(ModIdentifier.of("centrifuge_crank"));
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState object) {
        return ModIdentifier.of("geckolib/models/block/centrifuge_crank.geo.json");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState object) {
        return ModIdentifier.of("textures/block/centrifuge_crank.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull GeoAnimatable animatable) {
        return ModIdentifier.of("geckolib/animations/block/centrifuge_crank.animation.json");
    }
}
