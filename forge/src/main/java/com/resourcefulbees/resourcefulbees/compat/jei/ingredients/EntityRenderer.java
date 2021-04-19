package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && entityIngredient != null && mc.player != null) {
            CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(entityIngredient.getBeeType());
            EntityType<?> entityType = entityIngredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? EntityType.BEE : ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
            if (entityType != null) {
                Entity entity = entityType.create(mc.level);
                if (entity instanceof Bee) {
                    Bee beeEntity = (Bee) entity;
                    matrixStack.pushPose();
                    matrixStack.translate(8, 14, 0.5D);

                    beeEntity.tickCount = mc.player.tickCount;
                    beeEntity.yBodyRot = entityIngredient.getRotation() - 90;
                    float scaledSize = entityIngredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? 20 : 20 / beeData.getSizeModifier();
                    matrixStack.translate(x, y, 1);
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                    matrixStack.translate(0.0F, -0.2F, 1);
                    matrixStack.scale(-scaledSize, scaledSize, 30);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
                    EntityRenderDispatcher entityrenderermanager = mc.getEntityRenderDispatcher();
                    MultiBufferSource.BufferSource typeBuffer = mc.renderBuffers().bufferSource();
                    entityrenderermanager.render(beeEntity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, typeBuffer, 15728880);
                    typeBuffer.endBatch();
                    matrixStack.popPose();
                }
            }
        }
    }

    @NotNull
    @Override
    public List<Component> getTooltip(EntityIngredient entityIngredient, @Nonnull TooltipFlag iTooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        List<Component> desc = entityIngredient.getTooltip();
        if (desc != null){
            tooltip.addAll(desc);
        }
        return tooltip;
    }
}
