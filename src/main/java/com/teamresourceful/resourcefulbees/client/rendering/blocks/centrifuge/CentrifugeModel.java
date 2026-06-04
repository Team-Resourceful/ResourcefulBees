package com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CentrifugeModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {

    public CentrifugeModel() {
        super(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "centrifuge"));
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState object) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "geo/blocks/centrifuge.geo.json");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState animatable) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "textures/block/create_centrifuge.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull GeoAnimatable animatable) {
        return Identifier.fromNamespaceAndPath("resourcefulbees", "animations/centrifuge.animation.json");
    }
}
