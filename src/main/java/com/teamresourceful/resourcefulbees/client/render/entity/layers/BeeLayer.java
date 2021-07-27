package com.teamresourceful.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.render.LayerData;
import com.teamresourceful.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.enums.ModelType;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BeeLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private final LayerData layerData;

    private final CustomBeeModel<CustomBeeEntity> additionModel;
    private final Color color;

    public BeeLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> renderer, LayerData layerData) {
        super(renderer);
        this.layerData = layerData;
        this.color = layerData.getColor();
        this.additionModel = layerData.getModelType() == ModelType.DEFAULT ? null : new CustomBeeModel<>(layerData.getModelType());
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!customBeeEntity.hasNectar() && layerData.isPollen()) return;
        ResourceLocation texture = customBeeEntity.isAngry() ? layerData.getBeeTexture().getAngryTexture() : layerData.getBeeTexture().getNormalTexture();

        if (additionModel != null) {
            this.getParentModel().copyPropertiesTo(additionModel);
            additionModel.prepareMobModel(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
            additionModel.setupAnim(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

        renderLayers(matrixStackIn, bufferIn, packedLightIn, customBeeEntity, texture);
    }

    private void renderLayers(@NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, ResourceLocation texture) {
        if (layerData.isEnchanted()) {
            this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
            if (additionModel != null) {
                additionModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), 0.0F, 0.0F, 0.0F, 0.0F);
            }
        } else if (layerData.isEmissive()) {
            IVertexBuilder vertexConsumer = bufferIn.getBuffer(RenderType.eyes(texture));
            if (layerData.getPulseFrequency() == 0 || customBeeEntity.tickCount % layerData.getPulseFrequency() == 0.0f) {
                this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 1.0F);
                if (additionModel != null) {
                    additionModel.renderToBuffer(matrixStackIn, vertexConsumer, 15728640, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 1.0F);
                }
            }
        } else {
            renderColoredModel(this.getParentModel(), texture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue());
            if (additionModel != null) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
                additionModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 1.0F);
            }
        }
    }

    /***
     * Method overrides renderColoredCutoutModel from RenderLayer.class
     */
    protected static <T extends LivingEntity> void renderColoredModel(EntityModel<T> arg, ResourceLocation arg2, MatrixStack arg3, IRenderTypeBuffer arg4, int i, T arg5, float f, float g, float h) {
        IVertexBuilder lv = arg4.getBuffer(RenderType.entityTranslucent(arg2));
        arg.renderToBuffer(arg3, lv, i, LivingRenderer.getOverlayCoords(arg5, 0.0F), f, g, h, 1.0F);
    }
}
