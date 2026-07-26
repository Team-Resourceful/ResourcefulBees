package com.teamresourceful.resourcefulbees.client.rendering.entities.layers;

import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NonNull;

public class CustomBeeColoredLayer <R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<CustomBeeEntity, Void, @NonNull R> {
    public CustomBeeColoredLayer(GeoRenderer<CustomBeeEntity, Void, @NonNull R> renderer) {
        super(renderer);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<@NonNull R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.submitRenderTask(renderPassInfo, renderTasks);
    }
}
