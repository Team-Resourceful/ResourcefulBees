package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull PoseStack stack, @NotNull EntityIngredient ingredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && ingredient.getEntity() != null) {
            int y = 0;
            if (ingredient.getEntity() instanceof CustomBeeEntity) {
                y -= 5;
            }
            RenderUtils.renderEntity(stack, ingredient.getEntity(), -2, y, ingredient.getRotation(), 1);
        }
    }

    @NotNull
    @Override
    public List<Component> getTooltip(EntityIngredient entityIngredient, @NotNull TooltipFlag iTooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        tooltip.addAll(entityIngredient.getTooltip());
        return tooltip;
    }
}
