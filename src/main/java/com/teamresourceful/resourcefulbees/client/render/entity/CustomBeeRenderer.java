package com.teamresourceful.resourcefulbees.client.render.entity;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.client.render.entity.layers.BeeLayer;
import com.teamresourceful.resourcefulbees.client.render.entity.models.CustomBeeModel;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/empty_layer.png");

    public CustomBeeRenderer(EntityRendererManager manager, RenderData renderData) {
        super(manager, new CustomBeeModel<>(renderData.getBaseModelType()), 0.4F * renderData.getSizeModifier());
        renderData.getLayers().stream().limit(6).forEach(layerData -> addLayer(new BeeLayer(this, layerData)));
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull CustomBeeEntity entity) {
       return TEXTURE;
    }
}
