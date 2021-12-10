package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull PoseStack stack, int x, int y, @Nullable EntityIngredient entityIngredient) {
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
    public List<Component> getTooltip(EntityIngredient entityIngredient, @NotNull TooltipFlag iTooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityIngredient.getDisplayName());
        tooltip.addAll(entityIngredient.getTooltip());
        return tooltip;
    }
}
