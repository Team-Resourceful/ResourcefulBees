package com.teamresourceful.resourcefulbees.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import com.teamresourceful.resourcefulbees.client.rendering.pet.BeeRewardRender;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "submit*", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void resourcefulbees$renderPet(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState avatarState)) {
            return;
        }

        PetModelData pet = avatarState.getRenderData(BeeRewardRender.PET_DATA);

        if (pet != null) {
            BeeRewardRender.submit(pet, avatarState, poseStack, submitNodeCollector, camera);
        }
    }
}