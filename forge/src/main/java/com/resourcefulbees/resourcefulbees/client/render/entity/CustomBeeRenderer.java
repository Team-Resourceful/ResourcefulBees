package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.render.entity.layers.BeeLayer;
import com.resourcefulbees.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.resourcefulbees.resourcefulbees.api.beedata.RenderData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/empty_layer.png");

    public CustomBeeRenderer(EntityRenderDispatcher manager, RenderData renderData) {
        super(manager, new CustomBeeModel<>(renderData.getBaseModelType()), 0.4F * renderData.getSizeModifier());
        renderData.getLayers().forEach(layerData -> addLayer(new BeeLayer(this, layerData, renderData.getModelType())));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(CustomBeeEntity entity) {
       return TEXTURE;
    }
}
