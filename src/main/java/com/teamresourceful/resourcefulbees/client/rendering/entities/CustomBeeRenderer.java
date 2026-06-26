package com.teamresourceful.resourcefulbees.client.rendering.entities;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.reflect.TypeToken;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.client.rendering.entities.layers.CustomBeeLayer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CustomBeeRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<CustomBeeEntity, @NonNull R> {

    private static final DataTicket<Boolean> RBEES_IS_BABY_TICKET = DataTicket.create("is_baby", new TypeToken<>() {});

    public CustomBeeRenderer(EntityRendererProvider.Context ctx, BeeRenderData renderData) {
        super(ctx, new CustomBeeModel());
        renderData.layers().stream().limit(6).forEach(layerData -> withRenderLayer(new CustomBeeLayer<>(this, layerData)));
        float size = renderData.sizeModifier();
        withScale(size);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<@NonNull R> renderPassInfo, float widthScale, float heightScale) {
        if (renderPassInfo.getGeckolibData(RBEES_IS_BABY_TICKET)) {
            super.scaleModelForRender(renderPassInfo, widthScale * .5f, heightScale * .5f);
        }
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }

    @Override
    public void addRenderData(@NonNull CustomBeeEntity bee, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        renderState.addGeckolibData(RBEES_IS_BABY_TICKET, bee.isBaby());
    }

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
}
