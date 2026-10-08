package com.teamresourceful.resourcefulbees.client.rendering.pet;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.google.common.reflect.TypeToken;
import com.teamresourceful.resourcefulbees.client.pets.PetBeeModel;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PetBeeRenderer extends GeoObjectRenderer<PetModelData, Void, GeoRenderState> {

    public static final DataTicket<Identifier> MODEL = DataTicket.create("pet_model", new TypeToken<>() {});
    public static final DataTicket<Identifier> TEXTURE = DataTicket.create("pet_texture", new TypeToken<>() {});

    public PetBeeRenderer() {
        super(new PetBeeModel<>());
    }

    @Override
    public void addRenderData(@NonNull PetModelData animatable, @Nullable Void relatedObject, @NonNull GeoRenderState renderState, float partialTick) {
        renderState.addGeckolibData(MODEL, animatable.getModelLocation());
        renderState.addGeckolibData(TEXTURE, animatable.getTexture());
    }

    @Override
    public long getInstanceId(
            @NonNull PetModelData animatable,
            @Nullable Void relatedObject
    ) {
        return System.identityHashCode(animatable);
    }
}