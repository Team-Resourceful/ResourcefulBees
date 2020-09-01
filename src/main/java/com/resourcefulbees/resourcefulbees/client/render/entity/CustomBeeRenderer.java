package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.data.BeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;


@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    public CustomBeeRenderer(EntityRendererManager manager) {
        super(manager, new CustomBeeModel<>(), 0.4F);
                addLayer(new PrimaryColorLayer(this));
                addLayer(new SecondaryColorLayer(this));
                addLayer(new EmissiveBeeLayer(this));
    }

    @Nonnull
    public ResourceLocation getEntityTexture(CustomBeeEntity entity) {
        BeeData bee = entity.getBeeInfo();
        if (entity.hasAngerTime()) {
            return new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + bee.getBaseLayerTexture() + "_angry.png");
        }
        return new ResourceLocation(ResourcefulBees.MOD_ID, BeeConstants.ENTITY_TEXTURES_DIR + bee.getBaseLayerTexture() + ".png");
    }
}
