package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        if (Minecraft.getInstance().world != null && entityIngredient != null) {
            CustomBeeData beeData = BeeRegistry.getBeeData(entityIngredient.getBeeType());
            CustomBeeEntity bee = beeData.getEntityTypeRegistryObject().get().create(Minecraft.getInstance().world);
            Minecraft mc = Minecraft.getInstance();
            matrixStack.push();
            matrixStack.translate(8, 14, 0.5D);

            if (mc.player != null && bee != null) {
                bee.ticksExisted = mc.player.ticksExisted;
                bee.renderYawOffset = entityIngredient.getRotation() - 90;
                bee.setRenderingInJei(true);
                float scaledSize = 20 / bee.getBeeData().getSizeModifier();
                matrixStack.translate(x, y, 1);
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(-scaledSize, scaledSize, 30);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
                EntityRendererManager entityrenderermanager = mc.getRenderManager();
                IRenderTypeBuffer.Impl irendertypebuffer$impl = mc.getBufferBuilders().getEntityVertexConsumers();
                entityrenderermanager.render(bee, 0, 0, 0.0D, mc.getRenderPartialTicks(), 1, matrixStack, irendertypebuffer$impl, 15728880);
                irendertypebuffer$impl.draw();
            }
            matrixStack.pop();
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
