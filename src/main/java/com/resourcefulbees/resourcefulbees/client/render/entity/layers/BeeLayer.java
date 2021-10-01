package com.resourcefulbees.resourcefulbees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class BeeLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";
    //private static final Logger LOGGER = LogManager.getLogger();

    private final boolean isEmissive;
    private final int glowingPulse;
    private final boolean isEnchanted;
    private final CustomBeeModel<CustomBeeEntity> additionModel;
    private ResourceLocation layerTexture;
    private ResourceLocation angerLayerTexture;
    private float[] color;
    private final boolean isRainbowBee;

    public BeeLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> renderer, CustomBeeRenderer.LayerType layerType, ModelTypes addition, ColorData colorData) {
        super(renderer);
        this.additionModel = addition == ModelTypes.DEFAULT ? null : new CustomBeeModel<>(addition);
        this.isRainbowBee = colorData.isRainbowBee();
        this.isEnchanted = colorData.isEnchanted();
        this.glowingPulse = colorData.getGlowingPulse();

        switch (layerType) {
            case PRIMARY:
                this.isEmissive = false;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getPrimaryLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getPrimaryLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = isRainbowBee ? RainbowColor.getColorFloats() : colorData.getPrimaryColorFloats();
                break;
            case SECONDARY:
                this.isEmissive = false;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getSecondaryLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getSecondaryLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = isRainbowBee ? RainbowColor.getColorFloats() : colorData.getSecondaryColorFloats();
                break;
            case EMISSIVE:
                this.isEmissive = true;
                this.layerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + PNG_SUFFIX);
                this.angerLayerTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + colorData.getEmissiveLayerTexture() + ANGRY_PNG_SUFFIX);
                this.color = isRainbowBee ? RainbowColor.getColorFloats() : colorData.getGlowColorFloats();
                break;
            default:
                throw new IllegalStateException("You dun screwed up, did you add a new layer?");
        }
        if (!textureExists(layerTexture)) {
            layerTexture = BeeConstants.MISSING_TEXTURE;
        }
        if (!textureExists(angerLayerTexture)) {
            angerLayerTexture = layerTexture;
        }
    }

    public static boolean textureExists(ResourceLocation texture) {
        try {
            Minecraft.getInstance().getResourceManager().getResource(texture);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (isRainbowBee) color = RainbowColor.getColorFloats();
        ResourceLocation texture = customBeeEntity.isAngry() ? angerLayerTexture : layerTexture;

        if (additionModel != null) {
            this.getParentModel().copyPropertiesTo(additionModel);
            additionModel.prepareMobModel(customBeeEntity, limbSwing, limbSwingAmount, partialTicks);
            additionModel.setupAnim(customBeeEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

        if (isEmissive) {
            renderGlowingLayer(matrixStackIn, bufferIn, packedLightIn, customBeeEntity, texture);
        } else {
            renderColoredModel(this.getParentModel(), texture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, color[0], color[1], color[2]);
            if (additionModel != null) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(texture));
                additionModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), color[0], color[1], color[2], 1.0F);
            }
        }
    }

    private void renderGlowingLayer(@NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, ResourceLocation texture) {
        if (isEnchanted) {
            this.getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
            if (additionModel != null) {
                additionModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityGlint()), packedLightIn, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), 0.0F, 0.0F, 0.0F, 0.0F);
            }
        } else {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(texture));
            if (glowingPulse == 0 || customBeeEntity.tickCount / 5 % glowingPulse == 0) {
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
                if (additionModel != null) {
                    additionModel.renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, LivingRenderer.getOverlayCoords(customBeeEntity, 0.0F), color[0], color[1], color[2], 1.0F);
                }
            }
        }
    }

    protected static <T extends LivingEntity> void renderColoredModel(EntityModel<T> arg, ResourceLocation arg2, MatrixStack arg3, IRenderTypeBuffer arg4, int i, T arg5, float f, float g, float h) {
        IVertexBuilder lv = arg4.getBuffer(RenderType.entityTranslucent(arg2));
        arg.renderToBuffer(arg3, lv, i, LivingRenderer.getOverlayCoords(arg5, 0.0F), f, g, h, 1.0F);
    }
}
