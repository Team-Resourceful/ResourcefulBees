package com.dungeonderps.resourcefulbees.client.render.entity;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CustomBeeRenderer extends MobRenderer<CustomBeeEntity, CustomBeeModel<CustomBeeEntity>> {

    private static final ResourceLocation ANGRY_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/bee_angry.png");
    private static final ResourceLocation ANGRY_NECTAR_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/bee_angry_nectar.png");
    private static final ResourceLocation PASSIVE_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/bee.png");
    private static final ResourceLocation NECTAR_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID,"textures/entity/custom/bee_nectar.png");

    private static final ResourceLocation CREEPER_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/creeper/creeper_bee.png");
    private static final ResourceLocation CREEPER_SKIN_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/creeper/creeper_bee_angry.png");

    private static final ResourceLocation SKELETON_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/skeleton/skeleton_bee.png");
    private static final ResourceLocation SKELETON_SKIN_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/skeleton/skeleton_bee_angry.png");

    private static final ResourceLocation ZOMBIE_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/zombie/zombie_bee.png");
    private static final ResourceLocation ZOMBIE_SKIN_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/zombie/zombie_bee_angry.png");

    private static final ResourceLocation PIGMAN_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/pigman/pigman_bee.png");
    private static final ResourceLocation PIGMAN_SKIN_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/pigman/pigman_bee_angry.png");

    private static final ResourceLocation WITHER_SKIN = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/wither/wither_bee.png");
    private static final ResourceLocation WITHER_SKIN_ANGRY = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/wither/wither_bee_angry.png");

    public CustomBeeRenderer(EntityRendererManager manager) {
        super(manager, new CustomBeeModel<>(), 0.4F);
                addLayer(new CustomBeeOverlay(this));

    }

    public ResourceLocation getEntityTexture(CustomBeeEntity entity) {
        BeeData bee = entity.getBeeInfo();
        if (entity.isAngry()) {
            if (bee.isCreeperBee())
                return CREEPER_SKIN_ANGRY;
            if (bee.isSkeletonBee())
                return SKELETON_SKIN_ANGRY;
            if (bee.isZomBee())
                return ZOMBIE_SKIN_ANGRY;
            if (bee.isPigmanBee())
                return PIGMAN_SKIN_ANGRY;
            if (bee.isWitherBee())
                return WITHER_SKIN_ANGRY;
            return entity.hasNectar() ? ANGRY_NECTAR_SKIN : ANGRY_SKIN;
        } else {
            if (bee.isCreeperBee())
                return CREEPER_SKIN;
            if (bee.isSkeletonBee())
                return SKELETON_SKIN;
            if (bee.isZomBee())
                return ZOMBIE_SKIN;
            if (bee.isPigmanBee())
                return PIGMAN_SKIN;
            if (bee.isWitherBee())
                return WITHER_SKIN;
            return entity.hasNectar() ? NECTAR_SKIN : PASSIVE_SKIN;
        }
    }
}
