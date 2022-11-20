package com.teamresourceful.resourcefulbees.client.render.entities.layers;

import com.mojang.blaze3d.vertex.PoseStack;
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
            case NONE -> renderNone(stack, buffer, packedLight, bee, partialTicks, texture);
            case GLOW -> renderGlowLayer(stack, buffer, bee, partialTicks, texture);
            case ENCHANTED -> renderEnchantedLayer(stack, buffer, packedLight, bee, partialTicks);
        }

    }

    private void renderEnchantedLayer(PoseStack stack, MultiBufferSource buffer, int packedLight, E bee, float partialTicks) {
        renderLayer(bee, partialTicks, stack, buffer, RenderType.entityGlint(), packedLight, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    private void renderGlowLayer(PoseStack stack, MultiBufferSource buffer, E bee, float partialTicks, ResourceLocation texture) {
        if (layerData.pulseFrequency() == 0 || bee.tickCount % layerData.pulseFrequency() == 0.0f) {
            renderLayer(bee, partialTicks, stack, buffer, RenderType.eyes(texture), 15728640, OverlayTexture.NO_OVERLAY, layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
        }
    }

    private void renderNone(PoseStack stack, MultiBufferSource buffer, int packedLight, E bee, float partialTicks, ResourceLocation texture) {
        renderLayer(bee, partialTicks, stack, buffer, RenderType.entityTranslucent(texture), packedLight, LivingEntityRenderer.getOverlayCoords(bee, 0.0F), layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
    }

    private void renderLayer(E bee, float partialTicks, PoseStack stack, MultiBufferSource buffer, RenderType texture, int packedLight, int bee1, float layerData, float layerData1, float layerData2, float alpha) {
        renderer.render(this.getEntityModel().getModel(renderData.model()),
                bee, partialTicks,
                null, stack, null, buffer.getBuffer(texture),
                packedLight, bee1,
                layerData, layerData1, layerData2, alpha);
    }
}
