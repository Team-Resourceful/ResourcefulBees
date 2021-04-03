package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.render.entity.layers.AdditionLayer;
import com.resourcefulbees.resourcefulbees.client.render.entity.layers.BeeLayer;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BaseModelTypes;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private ResourceLocation angryTexture;
    private ResourceLocation baseTexture;

    public CustomBeeRenderer(BaseModelTypes modelType, EntityRendererManager manager, CustomBeeData beeData) {
        super(manager, new CustomBeeModel<>(modelType), 0.4F);

        angryTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + beeData.getBaseLayerTexture() + "_angry.png");
        baseTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + beeData.getBaseLayerTexture() + ".png");

        if (beeData.getColorData().getModelType() != ModelTypes.DEFAULT) {
            addLayer(new AdditionLayer<>(this, beeData.getColorData().getModelType(), angryTexture, baseTexture));
        }
        if (beeData.getColorData().isBeeColored()) {
            addLayer(new BeeLayer(this, LayerType.PRIMARY, beeData.getColorData().getModelType(), beeData.getColorData()));
            addLayer(new BeeLayer(this, LayerType.SECONDARY, beeData.getColorData().getModelType(), beeData.getColorData()));
        }
        if (beeData.getColorData().isGlowing() || beeData.getColorData().isEnchanted()) {
            addLayer(new BeeLayer(this, LayerType.EMISSIVE, beeData.getColorData().getModelType(), beeData.getColorData()));
        }

        if (!BeeLayer.textureExists(baseTexture)) {
            baseTexture = BeeConstants.MISSING_TEXTURE;
        }
        if (!BeeLayer.textureExists(angryTexture)) {
            angryTexture = baseTexture;
        }
    }

    @NotNull
    public ResourceLocation getTextureLocation(CustomBeeEntity entity) {
        return entity.isAngry() ? angryTexture : baseTexture;
    }

    public enum LayerType {
        PRIMARY,
        SECONDARY,
        EMISSIVE
    }

}
