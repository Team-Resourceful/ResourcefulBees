package com.resourcefulbees.resourcefulbees.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.client.render.patreon.LayerData;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetBeeModel;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetModelData;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class InterfaceBeeRenderer implements IGeoRenderer<PetModelData> {

    private final PetBeeModel<PetModelData> instance = new PetBeeModel<>();

    @Override
    public GeoModelProvider<PetModelData> getGeoModelProvider() {
        return instance;
    }

    @Override
    public ResourceLocation getTextureLocation(PetModelData instance) {
        return instance.getTexture();
    }

    public void render(PetModelData bee, @NotNull MatrixStack stack, float partialTicks, int tickCount, int x, int y, float modelScale, float rotation) {
        instance.setLivingAnimations(bee, bee.hashCode());
        stack.pushPose();
        stack.translate(x, y, 10 * modelScale);
        stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        stack.scale((20 * modelScale), (20 * modelScale), (20 * modelScale));
        stack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        stack.mulPose(Vector3f.XP.rotationDegrees(-10.0F));

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(bee.getTexture()));

        GeoModel model = instance.getModel(bee);

        render(model, bee, partialTicks,
                null, stack, null, vertexConsumer,
                200, OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(false)),
                1.0f, 1.0f, 1.0f, 1.0F);
        bee.getLayers().forEach(l -> renderLayer(l, model, bee, partialTicks, stack, tickCount, buffer));
        buffer.endBatch();
        stack.popPose();
    }

    private void renderLayer(LayerData layer, GeoModel model, PetModelData bee, float partialTicks, @NotNull MatrixStack stack, int tickCount, IRenderTypeBuffer buffer) {
        Color color = "rainbow".equals(layer.getColor()) ? new Color(RainbowColor.getRGB()) : Color.tryParse(layer.getColor(), -1);
        if (layer.isEnchanted()) {
            RenderType renderType = RenderType.entityGlint();
            render(model, bee, partialTicks,
                    renderType, stack, buffer, buffer.getBuffer(renderType),
                    100, OverlayTexture.NO_OVERLAY,
                    0.0F, 0.0F, 0.0F, 0.0F);
        } else if (layer.isEmissive()) {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.eyes(layer.getBeeTexture()));
            if (layer.getPulseFrequency() == 0 || tickCount % layer.getPulseFrequency() == 0.0f) {
                render(model, bee, partialTicks,
                        null, stack, null, vertexConsumer,
                        15728640, OverlayTexture.NO_OVERLAY,
                        color.getR(), color.getG(), color.getB(), 1.0F);
            }
        } else {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(layer.getBeeTexture()));
            render(model, bee, partialTicks,
                    null, stack, null, vertexConsumer,
                    100, OverlayTexture.pack(OverlayTexture.u(0f), OverlayTexture.v(false)),
                    color.getR(), color.getG(), color.getB(), 1.0F);
        }
    }
}
