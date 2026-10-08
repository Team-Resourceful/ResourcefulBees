package com.teamresourceful.resourcefulbees.client.rendering.pet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;

public final class BeeRewardRender {

    public static final ContextKey<PetModelData> PET_DATA = new ContextKey<>(ModIdentifier.of("pet_data"));

    private static final PetBeeRenderer RENDERER = new PetBeeRenderer();

    private BeeRewardRender() {
    }

    public static void submit(PetModelData pet, AvatarRenderState playerState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (playerState.isInvisible) {
            return;
        }

        poseStack.pushPose();

        poseStack.rotate(Axis.XP.rotationDegrees(180.0F));
        poseStack.scale(0.25F, 0.25F, 0.25F);

        float ageInTicks = playerState.ageInTicks;

        poseStack.rotate(Axis.YP.rotationDegrees((ageInTicks * 0.01F / 2.0F) * 360.0F));

        poseStack.translate(0.0F, 1.5F * Mth.sin(ageInTicks / 10.0F - 30.0F), 3.0F);

        poseStack.rotate(Axis.YP.rotationDegrees(-90.0F));

        RENDERER.performRenderPass(pet, null, poseStack, collector, camera, playerState.lightCoords, playerState.partialTick);

        poseStack.popPose();
    }
}