package com.teamresourceful.resourcefulbees.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EntityIngredient {

    private final CustomBeeData beeData;
    private final float rotation;
    private static final TranslatableComponent CREATOR_PREFIX = new TranslatableComponent("tooltip.resourcefulbees.bee.creator");

    public EntityIngredient(CustomBeeData beeData, float rotation) {
        this.beeData = beeData;
        this.rotation = rotation;
    }

    public CustomBeeData getBeeData() {
        return beeData;
    }

    public float getRotation() {
        return rotation;
    }

    public Component getDisplayName() {
        return new TranslatableComponent("entity.resourcefulbees." + beeData.getCoreData().getName() + "_bee");
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();

        if (beeData.getCoreData().getLore().isPresent()) {
            String lore = beeData.getCoreData().getLore().get();
            String[] loreTooltip = lore.split("\\r?\\n");
            for (String s: loreTooltip) {
                tooltip.add(new TextComponent(s).withStyle(beeData.getCoreData().getLoreColorStyle()));
            }
        }
        if (beeData.getCoreData().getCreator().isPresent()) {
            tooltip.add(CREATOR_PREFIX.copy().append(beeData.getCoreData().getCreator().get()).withStyle(ChatFormatting.GRAY));
        }
        String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new TextComponent(s).withStyle(ChatFormatting.GOLD));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(new TextComponent(beeData.getRegistryID().toString()).withStyle(ChatFormatting.DARK_GRAY));
        }
        return tooltip;
    }

    @Override
    public String toString() {
        return beeData.getCoreData().getName();
    }
}
