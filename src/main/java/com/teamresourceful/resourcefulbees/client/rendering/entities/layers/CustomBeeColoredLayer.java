package com.teamresourceful.resourcefulbees.client.rendering.entities.layers;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.client.rendering.entities.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class CustomBeeColoredLayer <R extends EntityRenderState & GeoRenderState> extends TextureLayerGeoLayer<CustomBeeEntity, Void, @NonNull R> {

    private final BeeLayerData layerData;

    public CustomBeeColoredLayer(GeoRenderer<CustomBeeEntity, Void, @NonNull R> renderer, BeeLayerData layerData) {
        super(renderer, layerData.texture().texture());
        this.layerData = layerData;
    }

    @Override
    protected @NonNull Identifier getTextureResource(@NonNull R renderState) {
        return layerData.texture().texture();
    }

    @Override
    public void submitRenderTask(@NonNull RenderPassInfo<@NonNull R> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        if (!layerData.pollenLayer() || layerData.pollenLayer() && renderPassInfo.renderState().getGeckolibData(CustomBeeRenderer.HAS_NECTAR)) {
            renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, layerData.color().getOpaqueValue());
            super.submitRenderTask(renderPassInfo, renderTasks);
            renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
        }
    }
}
