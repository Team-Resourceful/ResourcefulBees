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
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@Deprecated
@OnlyIn(Dist.CLIENT)
public class DragonLayer <T extends CustomBeeEntity> extends LayerRenderer<T, CustomBeeModel<T>> {

    private final EntityModel<T> dragonModel = new CustomBeeModel<>(ModelTypes.DRAGON);

    public DragonLayer(IEntityRenderer<T, CustomBeeModel<T>> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull T customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getEntityModel().setModelAttributes(this.dragonModel);
        this.dragonModel.setLivingAnimations(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
        this.dragonModel.setAngles(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(this.getTexture(customBeeEntity)));
        this.dragonModel.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlay(customBeeEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
