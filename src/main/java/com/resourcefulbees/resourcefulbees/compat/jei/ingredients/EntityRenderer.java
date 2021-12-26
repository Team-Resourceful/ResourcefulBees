package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull MatrixStack matrixStack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && entityIngredient != null && mc.player != null) {
            if (entityIngredient.getEntity() != null) {
                RenderUtils.renderEntity(matrixStack, entityIngredient.getEntity(), mc.level, x, y-5, -entityIngredient.getRotation(), 1);
            }
        }
    }

    @NotNull
    @Override
    public List<ITextComponent> getTooltip(EntityIngredient entityIngredient, @NotNull ITooltipFlag iTooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        List<ITextComponent> desc = entityIngredient.getTooltip();
        if (desc != null){
            tooltip.addAll(desc);
        }
        return tooltip;
    }
}
