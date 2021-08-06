package com.teamresourceful.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.client.render.entity.layers.CustomBeeLayer;
import com.teamresourceful.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CustomBeeRenderer<E extends CustomBeeEntity> extends GeoEntityRenderer<E> {

    public CustomBeeRenderer(EntityRendererManager renderManager, RenderData renderData) {
        super(renderManager, new CustomBeeModel<>());
        renderData.getLayers().stream().limit(6).forEach(layerData -> addLayer(new CustomBeeLayer<>(this, renderData, layerData)));
    }

    @Override
    public void render(GeoModel model, E bee, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.getBone("stinger").ifPresent(bone -> bone.setHidden(bee.hasStung()));
        super.render(model, bee, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }


    @Override
    public void renderEarly(E bee, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(bee, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
        stackIn.scale(bee.getRenderData().getSizeModifier(), bee.getRenderData().getSizeModifier(), bee.getRenderData().getSizeModifier());
        if (bee.isBaby()){
            stackIn.scale(0.5f, 0.5f, 0.5f);
        }
    }

    @Override
    public RenderType getRenderType(E animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
