package com.dungeonderps.resourcefulbees.client.render.entity;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
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
    }

    @Nonnull
    public ResourceLocation getEntityTexture(CustomBeeEntity entity) {
        BeeData bee = entity.getBeeInfo();
        if (entity.isAngry()) {
            return new ResourceLocation(ResourcefulBees.MOD_ID, BeeConst.ENTITY_TEXTURES_DIR + bee.getBaseLayerTexture() + "_angry.png");
        }
        return new ResourceLocation(ResourcefulBees.MOD_ID, BeeConst.ENTITY_TEXTURES_DIR + bee.getBaseLayerTexture() + ".png");
    }
}
