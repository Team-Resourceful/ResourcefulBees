package com.teamresourceful.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.client.render.entity.layers.CustomBeeLayer;
import com.teamresourceful.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CustomBeeRenderer<E extends CustomBeeEntity> extends GeoEntityRenderer<E> {

    public CustomBeeRenderer(EntityRendererProvider.Context ctx, RenderData renderData) {
        super(ctx, new CustomBeeModel<>());
        renderData.layers().stream().limit(6).forEach(layerData -> addLayer(new CustomBeeLayer<>(this, renderData, layerData)));
    }

    @Override
    public void render(GeoModel model, E bee, float partialTicks, RenderType type, PoseStack matrixStackIn, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.getBone("stinger").ifPresent(bone -> bone.setHidden(bee.hasStung()));
        super.render(model, bee, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }


    @Override
    public void renderEarly(E bee, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(bee, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
        stackIn.scale(bee.getRenderData().sizeModifier(), bee.getRenderData().sizeModifier(), bee.getRenderData().sizeModifier());
        if (bee.isBaby()){
            stackIn.scale(0.5f, 0.5f, 0.5f);
        }
    }

    @Override
    public RenderType getRenderType(E animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
