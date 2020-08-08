package com.dungeonderps.resourcefulbees.compat.jei.ingredients;

import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(int x, int y, @Nullable EntityIngredient entityIngredient) {
        if (Minecraft.getInstance().world !=null) {
            CustomBeeEntity bee = RegistryHandler.CUSTOM_BEE.get().create(Minecraft.getInstance().world);
            Minecraft mc = Minecraft.getInstance();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            matrixStack.translate(8, 17, 0.5D);

            if (mc.player != null && entityIngredient !=null && bee != null) {
                bee.ticksExisted = mc.player.ticksExisted;
                bee.renderYawOffset = entityIngredient.getRotation() - 90;
                bee.setBeeType(entityIngredient.getBeeType());
                float scaledSize = 20;
                if (!bee.getSizeModifierFromInfo(bee.getBeeType()).equals(1.0F)) {
                    scaledSize = 20 / bee.getSizeModifierFromInfo(bee.getBeeType());
                }
                matrixStack.translate(x, y, 1);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(scaledSize, scaledSize, 30);
                EntityRendererManager entityrenderermanager = mc.getRenderManager();
                IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getRenderTypeBuffers().getBufferSource();
                entityrenderermanager.renderEntityStatic(bee, 0, 0, 0.0D, mc.getRenderPartialTicks(), 1, matrixStack, irendertypebuffer$impl, 15728880);
                irendertypebuffer$impl.finish();
            }
            matrixStack.pop();
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltip(EntityIngredient entityIngredient, @Nonnull ITooltipFlag iTooltipFlag) {
        return Collections.singletonList(entityIngredient.getDisplayName());
    }
}
