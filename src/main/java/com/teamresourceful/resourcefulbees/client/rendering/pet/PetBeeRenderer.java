package com.teamresourceful.resourcefulbees.client.rendering.pet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class PetBeeRenderer extends GeoObjectRenderer<PetModelData> {

    private GeoModel<PetModelData> model;

    public PetBeeRenderer() {
        super(null);
    }

    @Override
    public GeoModel<PetModelData> getGeoModel() {
        return this.model;
    }

    public void setGeoModel(GeoModel<PetModelData> model) {
        this.model = model;
    }

    @Override
    public void actuallyRender(
        PoseStack poseStack, PetModelData data, BakedGeoModel model, RenderType renderType,
        MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
        int packedLight, int packedOverlay, float red, float green, float blue, float alpha
    ) {
        if (getGeoModel() == null) return;
        poseStack.pushPose();

        if (!isReRender) {
            AnimationState<PetModelData> animationState = new AnimationState<>(data, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);

            getGeoModel().addAdditionalStateData(animatable, instanceId, animationState::setData);
            getGeoModel().handleAnimations(animatable, instanceId, animationState);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        updateAnimatedTextureFrame(data);
        for (GeoBone group : model.topLevelBones()) {
            renderRecursively(poseStack, data, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
        }
        poseStack.popPose();
    }
}
