package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class EntityIngredient {

    private final String beeType;
    private final float rotation;

    public EntityIngredient(String beeType, float rotation) {
        this.beeType = beeType;
        this.rotation = rotation;
    }

    public String getBeeType() {
        return beeType;
    }

    public float getRotation() {
        return rotation;
    }

    public Component getDisplayName() {
        return new TranslatableComponent("entity.resourcefulbees." + beeType + "_bee");
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();

        String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new TextComponent(s).withStyle(ChatFormatting.GOLD));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(new TextComponent("resourcefulbees:" + beeType + "_bee").withStyle(ChatFormatting.DARK_GRAY));
        }
        return tooltip;
    }

}
