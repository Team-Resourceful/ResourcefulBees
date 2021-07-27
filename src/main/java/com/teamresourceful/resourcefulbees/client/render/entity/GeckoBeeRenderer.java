package com.teamresourceful.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.client.render.entity.models.GeckoBeeModel;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GeckoBeeRenderer<E extends CustomBeeEntity> extends GeoEntityRenderer<E> {

    public GeckoBeeRenderer(EntityRendererManager renderManager) {
        super(renderManager, new GeckoBeeModel<>());
    }

    @Override
    public RenderType getRenderType(E animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
