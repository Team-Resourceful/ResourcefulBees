package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.client.render.entity.GeckoBeeRenderer;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.io.IOException;

public class GeckoBeeLayer<E extends CustomBeeEntity> extends GeoLayerRenderer<E> {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";

    private final boolean isEmissive;
    private ResourceLocation layerTexture;
    private ResourceLocation angerLayerTexture;
    private final ColorData colorData;
    private float[] color;

    private final IGeoRenderer<E> renderer;

    public GeckoBeeLayer(IGeoRenderer<E> renderer, GeckoBeeRenderer.LayerType layerType, ColorData colorData) {
        super(renderer);
        this.colorData = colorData;
        this.renderer = renderer;


        switch (layerType) {
            case PRIMARY:
                this.isEmissive = false;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getPrimaryLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getPrimaryLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = colorData.isRainbowBee() ? RainbowColor.getColorFloats() : colorData.getPrimaryColorFloats();
                break;
            case SECONDARY:
                this.isEmissive = false;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getSecondaryLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getSecondaryLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = colorData.isRainbowBee() ? RainbowColor.getColorFloats() : colorData.getSecondaryColorFloats();
                break;
            case EMISSIVE:
                this.isEmissive = true;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = colorData.isRainbowBee() ? RainbowColor.getColorFloats() : colorData.getGlowColorFloats();
                break;
            default:
                throw new IllegalStateException("You dun screwed up, did you add a new layer?");
        }
        if (textureDoesNotExist(layerTexture)) {
            layerTexture = BeeConstants.MISSING_TEXTURE;
        }
        if (textureDoesNotExist(angerLayerTexture)) {
            angerLayerTexture = layerTexture;
        }
    }


    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, E bee, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (colorData.isRainbowBee()) color = RainbowColor.getColorFloats();

        ResourceLocation texture = bee.isAngry() ? angerLayerTexture : layerTexture;

        if (colorData.isEnchanted()) {
            renderer.render(this.getEntityModel().getModel(bee.getBeeData().getColorData().getModeLocation()),
                    bee, partialTicks,
                    null, stack, null, buffer.getBuffer(RenderType.entityGlint()),
                    packedLight, OverlayTexture.NO_OVERLAY,
                    0.0F, 0.0F, 0.0F, 0.0F);
        } else if (isEmissive) {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.eyes(texture));
            if (colorData.getGlowingPulse() == 0 || bee.tickCount % colorData.getGlowingPulse() == 0.0f) {
                renderer.render(this.getEntityModel().getModel(bee.getBeeData().getColorData().getModeLocation()),
                        bee, partialTicks,
                        null, stack, null, vertexConsumer,
                        15728640, OverlayTexture.NO_OVERLAY,
                        color[0], color[1], color[2], 1.0F);
            }
        } else {
            IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
            renderer.render(this.getEntityModel().getModel(bee.getBeeData().getColorData().getModeLocation()),
                    bee, partialTicks,
                    null, stack, null, vertexConsumer,
                    packedLight, LivingRenderer.getOverlayCoords(bee, 0.0F),
                    color[0], color[1], color[2], 1.0F);
        }

    }

    public static boolean textureDoesNotExist(ResourceLocation texture) {
        try {
            Minecraft.getInstance().getResourceManager().getResource(texture);
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
