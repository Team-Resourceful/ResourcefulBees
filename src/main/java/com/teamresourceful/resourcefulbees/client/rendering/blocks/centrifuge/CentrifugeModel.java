package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CentrifugeModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public CentrifugeModel() {
        super(ModIdentifier.of("centrifuge"));
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState object) {
        return ModIdentifier.of("geckolib/models/block/centrifuge.geo.json");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState animatable) {
        return ModIdentifier.of("textures/block/create_centrifuge.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull GeoAnimatable animatable) {
        return ModIdentifier.of("geckolib/animations/block/centrifuge.animation.json");
    }
}
