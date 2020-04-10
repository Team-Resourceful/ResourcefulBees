package com.dungeonderps.resourcefulbees.entity;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomBeeOverlay extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {
    private static final ResourceLocation BEE_COLLAR = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee_collar.png");
    private static final ResourceLocation BEE_COLLAR_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee_collar_angry.png");

    public CustomBeeOverlay(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn) {
        super(rendererIn);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CustomBeeEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float[] collarColor = entitylivingbaseIn.getBeeColorAsFloat();
        if(entitylivingbaseIn.isAngry()){
            renderCutoutModel(this.getEntityModel(), BEE_COLLAR_ANGRY, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, collarColor[0], collarColor[1], collarColor[2]);
        }
        else{
            renderCutoutModel(this.getEntityModel(), BEE_COLLAR, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, collarColor[0], collarColor[1], collarColor[2]);
        }
    }
}
