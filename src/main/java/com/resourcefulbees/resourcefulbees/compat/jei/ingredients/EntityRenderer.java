package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        if (Minecraft.getInstance().level != null && entityIngredient != null) {
            CustomBeeData beeData = BeeRegistry.getRegistry().getBeeData(entityIngredient.getBeeType());
            EntityType<?> entityType = entityIngredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? EntityType.BEE : ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
            if (entityType != null) {
                Entity entity = entityType.create(Minecraft.getInstance().level);
                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    Minecraft mc = Minecraft.getInstance();
                    matrixStack.pushPose();
                    matrixStack.translate(8, 14, 0.5D);

                    if (mc.player != null) {
                        beeEntity.tickCount = mc.player.tickCount;
                        beeEntity.yBodyRot = entityIngredient.getRotation() - 90;
                        float scaledSize = entityIngredient.getBeeType().equals(BeeConstants.VANILLA_BEE_TYPE) ? 20 : 20 / beeData.getSizeModifier();
                        matrixStack.translate(x, y, 1);
                        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                        matrixStack.translate(0.0F, -0.2F, 1);
                        matrixStack.scale(-scaledSize, scaledSize, 30);
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
                        EntityRendererManager entityrenderermanager = mc.getEntityRenderDispatcher();
                        IRenderTypeBuffer.Impl typeBuffer = mc.renderBuffers().bufferSource();
                        entityrenderermanager.render(beeEntity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, typeBuffer, 15728880);
                        typeBuffer.endBatch();
                    }
                    matrixStack.popPose();
                }
            }
        }
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltip(EntityIngredient entityIngredient, @Nonnull ITooltipFlag iTooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        List<ITextComponent> desc = entityIngredient.getTooltip();
        if (desc != null){
            tooltip.addAll(desc);
        }
        return tooltip;
    }
}
