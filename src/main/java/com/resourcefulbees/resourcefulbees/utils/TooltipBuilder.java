package com.resourcefulbees.resourcefulbees.utils;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class TooltipBuilder {

    List<ITextComponent> tooltip = new ArrayList<>();

    public TooltipBuilder addTip(String text) {
        tooltip.add(new StringTextComponent(text));
        return this;
    }

    public TooltipBuilder addTip(String text, TextFormatting formatting) {
        tooltip.add(new StringTextComponent(formatting + text));
        return this;
    }

    public TooltipBuilder applyStyle(TextFormatting formatting) {
        StringTextComponent textComponent = new StringTextComponent(formatting + tooltip.remove(tooltip.size() - 1).getString());
        tooltip.add(textComponent);
        return this;
    }

    public TooltipBuilder appendText(String text) {
        StringTextComponent textComponent = (StringTextComponent) tooltip.remove(tooltip.size() - 1);
        textComponent.appendString(text);
        tooltip.add(textComponent);
        return this;
    }

    public TooltipBuilder appendText(String text, TextFormatting formatting) {
        StringTextComponent textComponent = (StringTextComponent) tooltip.remove(tooltip.size() - 1);
        textComponent.appendString(formatting + text);
        tooltip.add(textComponent);
        return this;
    }

    public List<ITextComponent> build(){
        return tooltip.isEmpty() ? null : tooltip;
    }
}
