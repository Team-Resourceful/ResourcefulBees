package com.resourcefulbees.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.client.render.entity.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EmissiveBeeLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private final ResourceLocation emissiveLayerTexture;
    private final ResourceLocation angryEmissiveLayerTexture;
    ColorData colorData;

    public EmissiveBeeLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn, ColorData colorData) {
        super(rendererIn);
        this.emissiveLayerTexture = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + ".png");
        this.angryEmissiveLayerTexture = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + "_angry.png");
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(customBeeEntity.hasAngerTime() ? angryEmissiveLayerTexture : emissiveLayerTexture));

        if (colorData.isRainbowBee() && colorData.isGlowing()) {
            float[] glowColor = RainbowColor.getColorFloats();
            if (colorData.getGlowingPulse() == 0 || customBeeEntity.ticksExisted / 5 % colorData.getGlowingPulse() == 0) {
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.DEFAULT_UV, glowColor[0], glowColor[1], glowColor[2], 1.0F);
            }
        } else if (colorData.isGlowing() && colorData.hasGlowColor()){
            float[] glowColor = colorData.getGlowColorFloats();
            if (colorData.getGlowingPulse() == 0 || customBeeEntity.ticksExisted / 5 % colorData.getGlowingPulse() == 0) {
                this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.DEFAULT_UV, glowColor[0], glowColor[1], glowColor[2], 1.0F);
            }
        } else if (colorData.isEnchanted()){
            this.getEntityModel().render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityGlint()), packedLightIn, OverlayTexture.DEFAULT_UV, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}