package com.teamresourceful.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.api.beedata.render.LayerData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class CustomBeeLayer<E extends CustomBeeEntity> extends GeoLayerRenderer<E> {

    private final RenderData renderData;
    private final LayerData layerData;
    private final IGeoRenderer<E> renderer;

    public CustomBeeLayer(IGeoRenderer<E> renderer, RenderData renderData, LayerData layerData) {
        super(renderer);
        this.renderData = renderData;
        this.layerData = layerData;
        this.renderer = renderer;
    }


    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, E bee, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!bee.hasNectar() && layerData.isPollen()) return;
        ResourceLocation texture = layerData.beeTexture().getTexture(bee);

        switch (layerData.effect()) {
            case NONE -> {
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
                renderer.render(this.getEntityModel().getModel(renderData.model()),
                        bee, partialTicks,
                        null, stack, null, vertexConsumer,
                        packedLight, LivingEntityRenderer.getOverlayCoords(bee, 0.0F),
                        layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
            }
            case GLOW -> {
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(texture));
                if (layerData.pulseFrequency() == 0 || bee.tickCount % layerData.pulseFrequency() == 0.0f) {
                    renderer.render(this.getEntityModel().getModel(renderData.model()),
                            bee, partialTicks,
                            null, stack, null, vertexConsumer,
                            15728640, OverlayTexture.NO_OVERLAY,
                            layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
                }
            }
            case ENCHANTED -> {
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityGlint());
                renderer.render(this.getEntityModel().getModel(renderData.model()),
                        bee, partialTicks,
                        null, stack, null, vertexConsumer,
                        packedLight, OverlayTexture.NO_OVERLAY,
                        0.0F, 0.0F, 0.0F, 0.0F);
            }
        }

    }
}
