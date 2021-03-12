package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.*;

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

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("entity.resourcefulbees." + beeType + "_bee");
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();

        String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(new StringTextComponent("resourcefulbees:" + beeType + "_bee").withStyle(TextFormatting.DARK_GRAY));
        }
        return tooltip;
    }

}
