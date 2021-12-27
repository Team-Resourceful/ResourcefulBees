package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import java.util.Collections;


public class BeeRewardRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final PetBeeRenderer renderer = new PetBeeRenderer();

    public BeeRewardRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(@NotNull MatrixStack stack, @NotNull IRenderTypeBuffer buffer, int packedLightIn, @NotNull AbstractClientPlayerEntity playerEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!PetInfo.hasPet(playerEntity.getUUID()) || playerEntity.isInvisible()) return;
        PetModelData data = PetInfo.getPet(playerEntity.getUUID());
        if (data == null) return;

        stack.pushPose();

        stack.mulPose(Vector3f.XP.rotationDegrees(180));
        stack.scale(0.25f,0.25f,0.25f);
        stack.mulPose(Vector3f.YP.rotationDegrees((ageInTicks * 0.01F /2f)* 360f));
        stack.translate(0f,(1.5 * MathHelper.sin(ageInTicks/10 - 30f)),3f);
        stack.mulPose(Vector3f.YP.rotationDegrees(-90));

        RenderType cutoutNoCullRenderType = RenderType.entityCutoutNoCull(data.getTexture());
        IVertexBuilder ivertexbuilder = buffer.getBuffer(cutoutNoCullRenderType);

        @SuppressWarnings("unchecked")
        PetBeeModel<PetModelData> modelProvider = data.getModel();
        GeoModel model = modelProvider.getModel(data);

        AnimationEvent<PetModelData> event = new AnimationEvent<>(data, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.emptyList());
        modelProvider.setLivingAnimations(data, renderer.getUniqueID(data), event);
        renderer.render(model, data, partialTicks,
                cutoutNoCullRenderType, stack, buffer, ivertexbuilder,
                packedLightIn, OverlayTexture.NO_OVERLAY,
                1f,1f,1f,1f);

        for (LayerData layer : data.getLayers()) renderLayer(playerEntity, stack, buffer, layer, data, modelProvider.getModel(layer), partialTicks, packedLightIn);

        stack.popPose();
    }

    public void renderLayer(AbstractClientPlayerEntity playerEntity, MatrixStack stack, @NotNull IRenderTypeBuffer buffer, LayerData layerData, PetModelData data, GeoModel model, float partialTicks, int packedLightIn) {
        ResourceLocation texture = layerData.getBeeTexture();

        Color color = "rainbow".equals(layerData.getColor()) ? new Color(RainbowColor.getRGB()) : Color.tryParse(layerData.getColor(), -1);

        if (layerData.isEnchanted()) {
            RenderType renderType = RenderType.entityGlint();
            renderer.render(model, data, partialTicks,
                    renderType, stack, buffer, buffer.getBuffer(renderType),
                    packedLightIn, OverlayTexture.NO_OVERLAY,
                    0.0F, 0.0F, 0.0F, 0.0F);
        } else if (layerData.isEmissive()) {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.eyes(texture));
            if (layerData.getPulseFrequency() == 0 || playerEntity.tickCount % layerData.getPulseFrequency() == 0.0f) {
                renderer.render(model, data, partialTicks,
                        null, stack, null, vertexConsumer,
                        15728640, OverlayTexture.NO_OVERLAY,
                        color.getR(), color.getG(), color.getB(), 1.0F);
            }
        } else {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
            renderer.render(model, data, partialTicks,
                    null, stack, null, vertexConsumer,
                    packedLightIn, OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(false)),
                    color.getR(), color.getG(), color.getB(), 1.0F);
        }

    }
}