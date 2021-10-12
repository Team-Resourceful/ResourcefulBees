package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull MatrixStack stack, int x, int y, @Nullable EntityIngredient entityIngredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && entityIngredient != null && mc.player != null && entityIngredient.getEntity() != null) {
            if (entityIngredient.getEntity() instanceof CustomBeeEntity) {
                y -= 5;
            }
            RenderUtils.renderEntity(stack, entityIngredient.getEntity(), mc.level, x, y, entityIngredient.getRotation(), 1);
        }
    }

    @NotNull
    @Override
    public List<ITextComponent> getTooltip(EntityIngredient entityIngredient, @NotNull ITooltipFlag iTooltipFlag) {
        List<ITextComponent> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        tooltip.addAll(entityIngredient.getTooltip());
        return tooltip;
    }
}
