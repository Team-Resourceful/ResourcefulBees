package com.teamresourceful.resourcefulbees.client.rendering.entities.layers;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CustomBeeLayer<E extends CustomBeeEntity, R extends EntityRenderState & GeoRenderState> extends GeoRenderLayer<CustomBeeEntity, Void, @NonNull R> {

    private final BeeLayerData layerData;

    public CustomBeeLayer(GeoRenderer<CustomBeeEntity, Void, @NonNull R> renderer,  BeeLayerData layerData) {
        super(renderer);
        this.layerData = layerData;
    }

    @Override
    public void addRenderData(CustomBeeEntity bee, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        if (!bee.hasNectar() && layerData.pollenLayer()) return;
        switch (layerData.effect()) {
            case NONE -> renderState.addGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, true);
            case GLOW -> {
                if (layerData.pulseFrequency() == 0 || bee.tickCount % layerData.pulseFrequency() == 0.0f) {
                    renderState.addGeckolibData(DataTickets.PACKED_LIGHT, 15728640);
                }
            }
            case ENCHANTED -> renderState.addGeckolibData(DataTickets.IS_ENCHANTED, true);
        }

        renderState.addGeckolibData(DataTickets.RENDER_COLOR, layerData.color().getValue());
    }

    @Override
    public void submitRenderTask(@NonNull RenderPassInfo<@NonNull R> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        super.submitRenderTask(renderPassInfo, renderTasks);
    }

    //    @Override
//    public void render(PoseStack stack, E bee, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource buffer, VertexConsumer consumer, float partialTicks, int packedLight, int packedOverlay) {
//        if (!bee.hasNectar() && layerData.pollenLayer()) return;
//        Identifier texture = layerData.texture().getTexture(bee);
//
//        switch (layerData.effect()) {
//            case NONE -> renderNone(stack, buffer, packedLight, bee, partialTicks, texture);
//            case GLOW -> renderGlowLayer(stack, buffer, bee, partialTicks, texture);
//            case ENCHANTED -> renderEnchantedLayer(stack, buffer, packedLight, bee, partialTicks);
//        }
//    }
//
//    private void renderEnchantedLayer(PoseStack stack, MultiBufferSource buffer, int packedLight, E bee, float partialTicks) {
//        renderLayer(bee, partialTicks, stack, buffer, RenderType.entityGlint(), packedLight, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
//    }
//
//    private void renderGlowLayer(PoseStack stack, MultiBufferSource buffer, E bee, float partialTicks, Identifier texture) {
//        if (layerData.pulseFrequency() == 0 || bee.tickCount % layerData.pulseFrequency() == 0.0f) {
//            renderLayer(bee, partialTicks, stack, buffer, RenderType.eyes(texture), 15728640, OverlayTexture.NO_OVERLAY, layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
//        }
//    }
//
//    private void renderNone(PoseStack stack, MultiBufferSource buffer, int packedLight, E bee, float partialTicks, Identifier texture) {
//        renderLayer(bee, partialTicks, stack, buffer, RenderType.entityTranslucent(texture), packedLight, LivingEntityRenderer.getOverlayCoords(bee, 0.0F), layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
//    }
//
//    private void renderLayer(E bee, float partialTicks, PoseStack stack, MultiBufferSource buffer, RenderType texture, int packedLight, int bee1, float layerData, float layerData1, float layerData2, float alpha) {
//        renderer.reRender(this.getGeoModel().getBakedModel(renderData.model()),
//                stack, buffer, bee, texture, buffer.getBuffer(texture), partialTicks,
//                packedLight, bee1, new Color(layerData, layerData1, layerData2, alpha).getRGB()); //TODO dont create a new color object!
//                //layerData, layerData1, layerData2, alpha);
//    }
}
