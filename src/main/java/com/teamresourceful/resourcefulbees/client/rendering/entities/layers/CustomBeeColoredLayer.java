package com.teamresourceful.resourcefulbees.client.rendering.entities.layers;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NonNull;

public class CustomBeeColoredLayer <R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<CustomBeeEntity, Void, @NonNull R> {

    private final BeeLayerData layerData;

    public CustomBeeColoredLayer(GeoRenderer<CustomBeeEntity, Void, @NonNull R> renderer, BeeLayerData layerData) {
        super(renderer);
        this.layerData = layerData;
    }

    @Override
    public void preRender(RenderPassInfo<@NonNull R> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, layerData.color().getOpaqueValue());
        super.preRender(renderPassInfo, renderTasks);
    }
}
