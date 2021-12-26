package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GeckoBeeRenderer<E extends CustomBeeEntity> extends GeoEntityRenderer<E> {

    public GeckoBeeRenderer(EntityRendererManager renderManager, CustomBeeData beeData) {
        super(renderManager, new GeckoBeeModel<>());
        if (beeData.getColorData().isBeeColored()) {
            addLayer(new GeckoBeeLayer<>(this, LayerType.PRIMARY, beeData.getColorData()));
            addLayer(new GeckoBeeLayer<>(this, LayerType.SECONDARY, beeData.getColorData()));
        }
        if (beeData.getColorData().isGlowing() || beeData.getColorData().isEnchanted()) {
            addLayer(new GeckoBeeLayer<>(this, LayerType.EMISSIVE, beeData.getColorData()));
        }
    }

    @Override
    public void render(GeoModel model, E bee, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        model.getBone("stinger").ifPresent(bone -> bone.setHidden(bee.hasStung()));
        super.render(model, bee, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void render(@NotNull E bee, float entityYaw, float partialTicks, MatrixStack stack, @NotNull IRenderTypeBuffer buffer, int packedLightIn) {
        float size = bee.getBeeData().getSizeModifier();
        stack.scale(size, size, size);
        if (bee.isBaby()){
            stack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(bee, entityYaw, partialTicks, stack, buffer, packedLightIn);
    }

    @Override
    public RenderType getRenderType(E animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    public enum LayerType {
        PRIMARY,
        SECONDARY,
        EMISSIVE
    }
}

