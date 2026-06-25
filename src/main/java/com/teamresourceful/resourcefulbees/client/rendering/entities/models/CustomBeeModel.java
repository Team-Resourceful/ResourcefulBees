package com.teamresourceful.resourcefulbees.client.rendering.entities.models;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.google.common.reflect.TypeToken;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.geckolib.model.GeoModel;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CustomBeeModel<E extends CustomBeeEntity> extends GeoModel<E> {

    private static final DataTicket<Identifier> RBEES_TEXTURE_TICKET = DataTickets.create("rbees_texture", new TypeToken<>() {});
    private static final DataTicket<Identifier> RBEES_MODEL_TICKET = DataTicket.create("rbees_model", new TypeToken<>() {});
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
        return renderState.getGeckolibData(RBEES_MODEL_TICKET);
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return renderState.getGeckolibData(RBEES_TEXTURE_TICKET);
    }

    @Override
    public @NonNull Identifier getAnimationResource(E bee) {
        return bee.getRenderData().animations();
    }

    @Override
    public void addAdditionalStateData(@NonNull E bee, @Nullable Object relatedObject, @NonNull GeoRenderState renderState) {
        super.addAdditionalStateData(bee, relatedObject, renderState);
        renderState.addGeckolibData(RBEES_MODEL_TICKET, bee.getRenderData().model());
        renderState.addGeckolibData(RBEES_TEXTURE_TICKET, bee.getRenderData().texture().getTexture(bee));
    }
}
