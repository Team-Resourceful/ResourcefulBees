package com.teamresourceful.resourcefulbees.client.pets;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.client.rendering.pet.PetBeeRenderer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PetBeeModel<T extends PetModelData & GeoAnimatable> extends GeoModel<@NonNull T> {

    private static final Identifier ANIMATION = ModIdentifier.of("bee");

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return renderState.getGeckolibData(PetBeeRenderer.MODEL);
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return renderState.getGeckolibData(PetBeeRenderer.TEXTURE);
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull T animatable) {
        return ANIMATION;
    }
}