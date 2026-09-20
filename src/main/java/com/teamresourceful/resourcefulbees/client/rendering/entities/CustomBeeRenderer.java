package com.teamresourceful.resourcefulbees.client.rendering.entities;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;
import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.client.rendering.entities.layers.CustomBeeColoredLayer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.layers.CustomBeeGlintLayer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.layers.CustomBeeGlowLayer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CustomBeeRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<CustomBeeEntity, @NonNull R> {

    public static final DataTicket<Boolean> IS_ANGRY = DataTicket.create("is_angry", new TypeToken<>() {});
    private static final DataTicket<Boolean> IS_BABY = DataTicket.create("is_baby", new TypeToken<>() {});
    public static final DataTicket<Boolean> HAS_NECTAR = DataTicket.create("has_nectar", new TypeToken<>() {});
    private static final DataTicket<Boolean> HAS_STUNG = DataTicket.create("has_stung", new TypeToken<>() {});

    public CustomBeeRenderer(EntityRendererProvider.Context ctx, BeeRenderData renderData) {
        super(ctx, new CustomBeeModel());
        renderData.layers().stream().limit(6).forEach(layerData -> {
            switch (layerData.effect()) {
                case GLOW -> withRenderLayer(new CustomBeeGlowLayer<>(this, layerData));
                case ENCHANTED -> {
                    withRenderLayer(new CustomBeeGlintLayer<>(this, layerData));
                    withRenderLayer(new CustomBoneTextureGeoLayer<>(this, layerData.bone(), layerData.texture().texture()));
                }
                case null, default -> withRenderLayer(new CustomBeeColoredLayer<>(this, layerData));
            }
        });
        float size = renderData.sizeModifier();
        withScale(size);
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<@NonNull R> renderPassInfo, float widthScale, float heightScale) {
        if (renderPassInfo.getGeckolibData(IS_BABY)) {
            super.scaleModelForRender(renderPassInfo, widthScale * .5f, heightScale * .5f);
        }
        super.scaleModelForRender(renderPassInfo, widthScale, heightScale);
    }

    @Override
    public void addRenderData(@NonNull CustomBeeEntity bee, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        renderState.addGeckolibData(IS_BABY, bee.isBaby());
        renderState.addGeckolibData(IS_ANGRY, bee.isAngry());
        renderState.addGeckolibData(HAS_NECTAR, bee.hasNectar());
        renderState.addGeckolibData(HAS_STUNG, bee.hasStung());
    }

    @Override
    public int getRenderColor(@NonNull CustomBeeEntity animatable, @Nullable Void relatedObject, float partialTick) {
        return 0xFFFFFFFF;
    }

    @Override
    public void submit(@NonNull R renderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector renderTasks, @NonNull CameraRenderState cameraState) {
        renderState.addGeckolibData(DataTickets.RENDER_COLOR, 0xffffffff);
        super.submit(renderState, poseStack, renderTasks, cameraState);
    }

    @Override
    public @Nullable RenderType getRenderType(@NonNull R renderState, @NonNull Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public void adjustModelBonesForRender(@NonNull RenderPassInfo<@NonNull R> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);

        boolean hasStung = renderPassInfo.renderState().getOrDefaultGeckolibData(HAS_STUNG, false);

        snapshots.get("stinger").ifPresent(snapshot -> {
            snapshot.skipRender(hasStung);
            snapshot.skipChildrenRender(hasStung);
        });
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
