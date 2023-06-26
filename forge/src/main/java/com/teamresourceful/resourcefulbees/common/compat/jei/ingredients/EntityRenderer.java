package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull EntityIngredient ingredient) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && ingredient.getEntity() != null) {
            ClientRenderUtils.renderEntity(graphics, ingredient.getEntity(), -2, -2, ingredient.getRotation(), 1);
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
