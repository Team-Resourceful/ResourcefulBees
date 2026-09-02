package com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EntityRenderer implements IIngredientRenderer<EntityIngredient> {

    @Override
    public void render(@NotNull GuiGraphicsExtractor graphics, @NotNull EntityIngredient ingredient) {
        render(graphics, ingredient, 0, 0);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor graphics, @NonNull EntityIngredient ingredient, int posX, int posY){
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        Entity entity = ingredient.entity();
        if (entity == null) {
            return;
        }

        ClientRenderUtils.preparePreviewEntity(entity);
        ClientRenderUtils.renderEntity(graphics, entity, posX, posY, 16, 16, ingredient.rotation(), 0.75f);
    }

    @Override
    public @NotNull List<Component> getTooltip(@NotNull EntityIngredient ingredient, @NotNull TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(ingredient.getDisplayName());
        tooltip.addAll(ingredient.getTooltip());

        return tooltip;
    }
}