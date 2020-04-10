package com.dungeonderps.resourcefulbees.entity;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private static final ResourceLocation field_229040_a_ = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee_angry.png");
    private static final ResourceLocation field_229041_g_ = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee_angry_nectar.png");
    private static final ResourceLocation field_229042_h_ = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee.png");
    private static final ResourceLocation field_229043_i_ = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/bee_nectar.png");

    public CustomBeeRenderer(EntityRendererManager p_i226033_1_) {
        super(p_i226033_1_, new CustomBeeModel<>(), 0.4F);
        addLayer(new CustomBeeOverlay(this));
    }

    public ResourceLocation getEntityTexture(CustomBeeEntity entity) {
        if (entity.isAngry()) {
            return entity.hasNectar() ? field_229041_g_ : field_229040_a_;
        } else {
            return entity.hasNectar() ? field_229043_i_ : field_229042_h_;
        }
    }

}
