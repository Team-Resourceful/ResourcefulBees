//package com.teamresourceful.resourcefulbees.client.rendering.entities;
//
//import com.geckolib.cache.model.BakedGeoModel;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
//import com.teamresourceful.resourcefulbees.client.rendering.entities.layers.CustomBeeLayer;
//import com.teamresourceful.resourcefulbees.client.rendering.entities.models.CustomBeeModel;
//import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.client.renderer.rendertype.RenderType;
//import net.minecraft.resources.Identifier;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import com.geckolib.renderer.GeoEntityRenderer;
//
//public class CustomBeeRenderer<E extends CustomBeeEntity> extends GeoEntityRenderer<E> {
//
//    public CustomBeeRenderer(EntityRendererProvider.Context ctx, BeeRenderData renderData) {
//        super(ctx, new CustomBeeModel<>());
//        renderData.layers().stream().limit(6).forEach(layerData -> addRenderLayer(new CustomBeeLayer<>(this, renderData, layerData)));
//    }
//
//    @Override
//    public void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, E bee, RenderType renderType, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int color) {
//        model.getBone("stinger").ifPresent(bone -> bone.setHidden(bee.hasStung()));
//        super.reRender(model, poseStack, bufferSource, animatable, renderType, buffer, partialTick, packedLight, packedOverlay, color);
//    }
//
//    @Override
//    public void render(E bee, float entityYaw, float partialTick, PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight) {
//        float size = bee.getRenderData().sizeModifier();
//        stack.scale(size, size, size);
//        if (bee.isBaby()){
//            stack.scale(0.5f, 0.5f, 0.5f);
//        }
//        super.render(bee, entityYaw, partialTick, stack, buffer, packedLight);
//    }
//
//    @Override
//    public RenderType getRenderType(E animatable, Identifier texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
//        return RenderType.entityTranslucent(getTextureLocation(animatable));
//    }
//}
