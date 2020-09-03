package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.CustomBee;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.RainbowColor;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EmissiveBeeLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    public EmissiveBeeLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn) {
            super(rendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, CustomBeeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        CustomBee bee = entitylivingbaseIn.getBeeInfo();
        ResourceLocation location = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + bee.ColorData.getEmissiveLayerTexture() + (entitylivingbaseIn.hasAngerTime() ? "_angry.png" : ".png"));
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(location));

        if (bee.ColorData.isRainbowBee() && bee.ColorData.isGlowing()) {
            float[] glowColor = RainbowColor.getColorFloats();
            if (!entitylivingbaseIn.getRenderingInJei() && (bee.ColorData.getGlowingPulse() == 0 || entitylivingbaseIn.ticksExisted / 5 % bee.ColorData.getGlowingPulse() == 0)) {
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.DEFAULT_UV, glowColor[0], glowColor[1], glowColor[2], 1.0F);
            }
        } else if (bee.ColorData.isGlowing() && bee.ColorData.getGlowColor() !=null && !bee.ColorData.getGlowColor().isEmpty()){
            float[] glowColor = BeeRegistry.getColorFloats(bee.ColorData.getGlowColor());
            if (!entitylivingbaseIn.getRenderingInJei() && (bee.ColorData.getGlowingPulse() == 0 || entitylivingbaseIn.ticksExisted / 5 % bee.ColorData.getGlowingPulse() == 0)) {
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.DEFAULT_UV, glowColor[0], glowColor[1], glowColor[2], 1.0F);
            }
        } else if (bee.ColorData.isEnchanted()){
            this.getEntityModel().render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityGlint()), packedLightIn, OverlayTexture.DEFAULT_UV, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}