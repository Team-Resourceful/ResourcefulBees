package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PrimaryColorLayer extends LayerRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    //private final ColorData colorData;
    private final ResourceLocation primaryLayerTexture;
    private float[] primaryColor;
    private final boolean isRainbowBee;

    public PrimaryColorLayer(IEntityRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> rendererIn, ColorData colorData) {
        super(rendererIn);
        //this.colorData = colorData;
        isRainbowBee = colorData.isRainbowBee() && colorData.isGlowing();
        primaryLayerTexture = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + colorData.getPrimaryLayerTexture() + ".png");
        primaryColor = isRainbowBee ? RainbowColor.getColorFloats() : colorData.getPrimaryColorFloats();
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @NotNull CustomBeeEntity customBeeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (isRainbowBee) primaryColor = RainbowColor.getColorFloats();
        renderModel(this.getEntityModel(), primaryLayerTexture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, primaryColor[0], primaryColor[1], primaryColor[2]);

        /*        CustomBeeData beeData = customBeeEntity.getBeeData();

        if (beeData.getColorData().isBeeColored()) {
            if (beeData.getColorData().isRainbowBee() && !beeData.getColorData().isGlowing()) {
                float[] primaryColor = RainbowColor.getColorFloats();
                ResourceLocation location = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + beeData.getColorData().getPrimaryLayerTexture() + ".png");
                renderModel(this.getEntityModel(), primaryLayerTexture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, primaryColor[0], primaryColor[1], primaryColor[2]);
            } else if (beeData.getColorData().hasPrimaryColor()) {
                float[] primaryColor = beeData.getColorData().getPrimaryColorFloats();
                ResourceLocation location = new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + beeData.getColorData().getPrimaryLayerTexture() + ".png");
                renderModel(this.getEntityModel(), primaryLayerTexture, matrixStackIn, bufferIn, packedLightIn, customBeeEntity, primaryColor[0], primaryColor[1], primaryColor[2]);
            }
        }*/
    }
}
