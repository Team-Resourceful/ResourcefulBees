package com.resourcefulbees.resourcefulbees.compat.jei.ingredients;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class EntityIngredient {

    private final String beeType;
    private final float rotation;

    public EntityIngredient(String beeType, float rotation) {
        this.beeType = beeType;
        this.rotation = rotation;
    }

    public String getBeeType() { return beeType; }
    public float getRotation() { return rotation; }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("entity.resourcefulbees."+ beeType + "_bee");
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();

        String desc = I18n.format("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new StringTextComponent(s).formatted(TextFormatting.GOLD));
        }
        return tooltip;

        //saving old format just in case
/*        if (!I18n.hasKey("tooltip.resourcefulbees.jei."+ beeType)){
            return null;
        } else {
            String desc = I18n.format("tooltip.resourcefulbees.jei."+ beeType);
            String[] descTooltip = desc.split("\\r?\\n");
            for (String s : descTooltip) {
                tooltip.add(new StringTextComponent(s));
            }
            tooltip.add(new TranslationTextComponent("tooltip.resourcefulbees.jei.click_bee_info"));
            return tooltip;
        }*/
    }

}
