package com.teamresourceful.resourcefulbees.client.pets;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PetBeeModel<T extends PetModelData & GeoAnimatable> extends GeoModel<@NonNull T> {

    private static final Identifier ANIMATION = ModIdentifier.of("animation/bee.animation.json");

    public BakedGeoModel getModel(PetModelData object){
        return this.getBakedModel(getModelResource(object));
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return null;
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return null;
    }





    public Identifier getModelResource(PetModelData object) {
        return object.getModelLocation();
    }

    public Identifier getTextureResource(PetModelData object) {
        return object.getTexture();
    }






    @Override
    public @NonNull Identifier getAnimationResource(@NonNull PetModelData animatable) {
        return ANIMATION;
    }
}
