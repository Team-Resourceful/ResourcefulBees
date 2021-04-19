package com.resourcefulbees.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class AdditionLayer<T extends CustomBeeEntity> extends RenderLayer<T, CustomBeeModel<T>> {

    private final CustomBeeModel<T> additionModel;
    private ResourceLocation baseTexture;
    private ResourceLocation angryTexture;

    public AdditionLayer(RenderLayerParent<T, CustomBeeModel<T>> rendererIn, ModelTypes type, ResourceLocation angryTexture, ResourceLocation baseTexture) {
        super(rendererIn);
        additionModel = new CustomBeeModel<>(type);
        this.baseTexture = baseTexture;
        this.angryTexture = angryTexture;
        if (!BeeLayer.textureExists(baseTexture) || baseTexture == null) {
            this.baseTexture = BeeConstants.MISSING_TEXTURE;
        }
        if (!BeeLayer.textureExists(angryTexture) || angryTexture == null) {
            this.angryTexture = this.baseTexture;
        }
    }


    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = customBeeEntity.isAngry() ? angryTexture : baseTexture;
        this.getParentModel().copyPropertiesTo(this.additionModel);
        this.additionModel.prepareMobModel(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
        this.additionModel.setupAnim(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
        this.additionModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(customBeeEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
