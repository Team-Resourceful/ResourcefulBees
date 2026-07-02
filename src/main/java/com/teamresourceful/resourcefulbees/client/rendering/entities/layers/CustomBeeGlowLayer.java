package com.teamresourceful.resourcefulbees.client.rendering.entities.layers;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.google.common.reflect.TypeToken;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CustomBeeGlowLayer <R extends EntityRenderState & GeoRenderState> extends AutoGlowingGeoLayer<CustomBeeEntity, Void, @NonNull R> {

    public static final DataTicket<Integer> RBEES_BRIGHTNESS_TICKET = DataTicket.create("brightness", new TypeToken<>() {});

    private final BeeLayerData layerData;
    public CustomBeeGlowLayer(GeoRenderer<CustomBeeEntity, Void, @NonNull R> renderer, BeeLayerData layerData) {
        super(renderer);
        this.layerData = layerData;
    }

    @Override
    protected boolean shouldRespectWorldLighting(@NonNull R renderState) {
        return true;
    }

    @Override
    protected int getBrightness(@NonNull R renderState) {
        return renderState.getGeckolibData(RBEES_BRIGHTNESS_TICKET);
    }

    @Override
    public void addRenderData(CustomBeeEntity animatable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        var pulseFrequency = animatable.getRenderData().pulseFrequency();
        int brightness = (pulseFrequency == 0 || animatable.tickCount % pulseFrequency == 0) ? LightCoordsUtil.FULL_SKY : 0;
        renderState.addGeckolibData(RBEES_BRIGHTNESS_TICKET, brightness);

    }

    @Override
    protected @NonNull Identifier getTextureResource(@NonNull R renderState) {
        return layerData.texture().texture();
    }

    @Override
    public void preRender(RenderPassInfo<@NonNull R> renderPassInfo, SubmitNodeCollector renderTasks) {
        renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, layerData.color().getValue());
        super.preRender(renderPassInfo, renderTasks);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<@NonNull R> renderPassInfo, SubmitNodeCollector renderTasks) {
        super.submitRenderTask(renderPassInfo, renderTasks);
        renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFFFF);
    }
}
