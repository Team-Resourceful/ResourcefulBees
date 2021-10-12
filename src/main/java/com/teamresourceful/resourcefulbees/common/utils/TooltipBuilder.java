package com.teamresourceful.resourcefulbees.common.utils;

import net.minecraft.util.text.*;

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

    public TooltipBuilder addTranslatableTip(String key, TextFormatting formatting) {
        tooltip.add(new TranslationTextComponent(key).withStyle(formatting));
        return this;
    }
    public TooltipBuilder addTranslatableTip(String key) {
        tooltip.add(new TranslationTextComponent(key));
        return this;
    }

    public TooltipBuilder applyStyle(TextFormatting formatting) {
        ((IFormattableTextComponent)tooltip.get(tooltip.size() - 1)).withStyle(formatting);
        return this;
    }

    public TooltipBuilder appendText(String text) {
        ((IFormattableTextComponent)tooltip.get(tooltip.size() - 1)).append(text);
        return this;
    }

    public TooltipBuilder appendText(String text, TextFormatting formatting) {
        ((IFormattableTextComponent)tooltip.get(tooltip.size() - 1)).append(formatting + text);
        return this;
    }

    public TooltipBuilder appendTranslatableText(String key) {
        ((IFormattableTextComponent)tooltip.get(tooltip.size() - 1)).append(new TranslationTextComponent(key));
        return this;
    }

    public TooltipBuilder appendTranslatableText(String key, TextFormatting formatting) {
        ((IFormattableTextComponent)tooltip.get(tooltip.size() - 1)).append(new TranslationTextComponent(key).withStyle(formatting));
        return this;
    }

    public List<ITextComponent> build(){
        return tooltip.isEmpty() ? null : tooltip;
    }
}
