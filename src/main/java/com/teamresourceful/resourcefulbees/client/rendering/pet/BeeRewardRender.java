package com.teamresourceful.resourcefulbees.client.rendering.pet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.client.pets.PetBeeModel;
import com.teamresourceful.resourcefulbees.client.pets.PetInfo;
import com.teamresourceful.resourcefulbees.client.pets.PetModelData;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;


public class BeeRewardRender extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final PetBeeRenderer renderer = new PetBeeRenderer();

    public BeeRewardRender(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLightIn, @NotNull AbstractClientPlayer playerEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!PetInfo.hasPet(playerEntity.getUUID()) || playerEntity.isInvisible()) return;
        PetModelData data = PetInfo.getPet(playerEntity.getUUID());
        if (data == null) return;

        try (var ignored = new CloseablePoseStack(stack)) {

            stack.mulPose(Axis.XP.rotationDegrees(180));
            stack.scale(0.25f, 0.25f, 0.25f);
            stack.mulPose(Axis.YP.rotationDegrees((ageInTicks * 0.01F / 2f) * 360f));
            stack.translate(0f, (1.5 * Mth.sin(ageInTicks / 10 - 30f)), 3f);
            stack.mulPose(Axis.YP.rotationDegrees(-90));

            RenderType renderType = RenderType.entityCutoutNoCull(data.getTexture());
            VertexConsumer consumer = buffer.getBuffer(renderType);

            PetBeeModel<PetModelData> provider = data.getModel();
            BakedGeoModel model = provider.getModel(data);

            renderer.setGeoModel(provider);

            AnimationState<PetModelData> event = new AnimationState<>(data, 0, 0, Minecraft.getInstance().getFrameTime(), true);
            provider.setCustomAnimations(data, renderer.getInstanceId(data), event);
            renderer.reRender(model, stack, buffer, data, renderType, consumer, partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

            for (BeeLayerData layer : data.getLayers()) {
                renderLayer(playerEntity, stack, buffer, layer, data, model, partialTicks, packedLightIn);
            }
        }
    }

    public void renderLayer(AbstractClientPlayer playerEntity, PoseStack stack, @NotNull MultiBufferSource buffer, BeeLayerData layerData, PetModelData data, BakedGeoModel model, float partialTicks, int packedLightIn) {
        ResourceLocation texture = layerData.texture().texture();

        switch (layerData.effect()) {
            case NONE -> {
                RenderType type = RenderType.entityTranslucent(texture);
                VertexConsumer consumer = buffer.getBuffer(type);
                renderer.reRender(
                    model, stack, buffer, data,
                    type, consumer, partialTicks, packedLightIn, OverlayTexture.NO_OVERLAY,
                    layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
            }
            case ENCHANTED -> {
                RenderType type = RenderType.entityGlint();
                VertexConsumer consumer = buffer.getBuffer(type);
                renderer.reRender(
                    model, stack, buffer, data,
                    type, consumer, partialTicks,
                    packedLightIn, OverlayTexture.NO_OVERLAY,
                    0.0F, 0.0F, 0.0F, 0.0F);
            }
            case GLOW -> {
                RenderType type = RenderType.eyes(texture);
                VertexConsumer consumer = buffer.getBuffer(type);
                if (layerData.pulseFrequency() == 0 || playerEntity.tickCount % layerData.pulseFrequency() == 0.0f) {
                    renderer.reRender(
                        model, stack, buffer, data,
                        type, consumer, partialTicks,
                        LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY,
                        layerData.color().getFloatRed(), layerData.color().getFloatGreen(), layerData.color().getFloatBlue(), 1.0F);
                }
            }
        }

    }
}
