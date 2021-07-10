package com.teamresourceful.resourcefulbees.compat.jei.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && entityIngredient != null && mc.player != null && entityIngredient.getEntity() != null) {
            RenderData coreData = entityIngredient.getBeeData().getRenderData();
            if (entityIngredient.getEntity() instanceof Bee) {
                Bee beeEntity = (Bee) entityIngredient.getEntity();
                matrixStack.pushPose();
                matrixStack.translate(8, 14, 0.5D);

                beeEntity.tickCount = mc.player.tickCount;
                beeEntity.yBodyRot = entityIngredient.getRotation() - 90;
                float scaledSize = 20 / coreData.getSizeModifier();
                matrixStack.translate(x, y, 1);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(-scaledSize, scaledSize, 30);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
                EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource typeBuffer = mc.renderBuffers().bufferSource();
                entityRenderDispatcher.render(beeEntity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, typeBuffer, 15728880);
                typeBuffer.endBatch();
                matrixStack.popPose();
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
