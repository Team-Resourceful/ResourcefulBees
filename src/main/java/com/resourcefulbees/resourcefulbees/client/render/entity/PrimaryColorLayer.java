package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PrimaryColorLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    public PrimaryColorLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn) {
        super(rendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, CustomBeeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BeeData bee = entitylivingbaseIn.getBeeInfo();
        if (bee.isBeeColored() && bee.getPrimaryColor() != null && !bee.getPrimaryColor().isEmpty()) {
            float[] primaryColor = BeeInfo.getBeeColorAsFloat(bee.getPrimaryColor());
            ResourceLocation location = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + bee.getPrimaryLayerTexture() + ".png");
            renderCutoutModel(this.getEntityModel(), location, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, primaryColor[0], primaryColor[1], primaryColor[2]);
        }
    }
}
