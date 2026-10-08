package com.teamresourceful.resourcefulbees.client.model.armor;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.items.BeekeeperArmorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class BeekeeperArmorModel extends GeoModel<BeekeeperArmorItem> {

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return ModIdentifier.of("armor/beekeeper_suit");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return ModIdentifier.of("textures/armor/beekeeper_suit.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull BeekeeperArmorItem animatable) {
        return null;
    }

}