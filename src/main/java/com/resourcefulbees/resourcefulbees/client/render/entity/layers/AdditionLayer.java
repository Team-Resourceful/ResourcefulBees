package com.resourcefulbees.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class AdditionLayer<T extends CustomBeeEntity> extends LayerRenderer<T, CustomBeeModel<T>> {

    private final CustomBeeModel<T> additionModel;
    private final ResourceLocation baseTexture;
    private final ResourceLocation angryTexture;

    public AdditionLayer(IEntityRenderer<T, CustomBeeModel<T>> rendererIn, ModelTypes type, ResourceLocation angryTexture, ResourceLocation baseTexture) {
        super(rendererIn);
        additionModel = new CustomBeeModel<>(type);
        this.baseTexture = baseTexture;
        this.angryTexture = angryTexture;
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull T customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = customBeeEntity.hasAngerTime() ? angryTexture : baseTexture;
        this.getEntityModel().setModelAttributes(this.additionModel);
        this.additionModel.setLivingAnimations(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
        this.additionModel.setAngles(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(texture));
        this.additionModel.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlay(customBeeEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
