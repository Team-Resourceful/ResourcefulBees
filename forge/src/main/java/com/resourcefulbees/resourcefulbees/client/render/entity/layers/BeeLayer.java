package com.resourcefulbees.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.api.beedata.LayerData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BeeLayer extends RenderLayer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private final LayerData layerData;

    private final CustomBeeModel<CustomBeeEntity> additionModel;
    private float[] color;

    public BeeLayer(RenderLayerParent<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> renderer, LayerData layerData, ModelTypes modelType) {
        super(renderer);
        this.layerData = layerData;
        this.color = layerData.getColor().getRGBComponents(null);
        this.additionModel = modelType == ModelTypes.DEFAULT ? null : new CustomBeeModel<>(modelType);
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (layerData.isRainbow()) color = RainbowColor.getColorFloats();
        ResourceLocation texture = customBeeEntity.isAngry() ? layerData.getBeeTexture().getAngryTexture() : layerData.getBeeTexture().getNormalTexture();

        if (additionModel != null) {
            this.getParentModel().copyPropertiesTo(additionModel);
            additionModel.prepareMobModel(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
            additionModel.setupAnim(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

        renderLayers(matrixStackIn, bufferIn, packedLightIn, customBeeEntity, texture);
    }

    private void renderLayers(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, ResourceLocation texture) {
        if (layerData.isEnchanted()) {
            this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
            if (additionModel != null) {
                additionModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, LivingEntityRenderer.getOverlayCoords(customBeeEntity, 0.0F), 0.0F, 0.0F, 0.0F, 0.0F);
            }
        } else if (layerData.isEmissive()) {
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.eyes(texture));
            if (layerData.getPulseFrequency() == 0 || customBeeEntity.tickCount % layerData.getPulseFrequency() == 0.0f) {
                this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
                if (additionModel != null) {
                    additionModel.renderToBuffer(matrixStackIn, vertexConsumer, 15728640, LivingEntityRenderer.getOverlayCoords(customBeeEntity, 0.0F), color[0], color[1], color[2], 1.0F);
                }
            }
        } else {
            renderColoredModel(this.getParentModel(), texture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, color[0], color[1], color[2]);
            if (additionModel != null) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
                additionModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(customBeeEntity, 0.0F), color[0], color[1], color[2], 1.0F);
            }
        }
    }

    /***
     * Method overrides renderColoredCutoutModel from RenderLayer.class
     */
    protected static <T extends LivingEntity> void renderColoredModel(EntityModel<T> arg, ResourceLocation arg2, PoseStack arg3, MultiBufferSource arg4, int i, T arg5, float f, float g, float h) {
        VertexConsumer lv = arg4.getBuffer(RenderType.entityTranslucent(arg2));
        arg.renderToBuffer(arg3, lv, i, LivingEntityRenderer.getOverlayCoords(arg5, 0.0F), f, g, h, 1.0F);
    }
}
