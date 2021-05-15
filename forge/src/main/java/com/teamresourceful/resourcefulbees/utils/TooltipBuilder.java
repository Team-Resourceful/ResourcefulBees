package com.teamresourceful.resourcefulbees.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class TooltipBuilder {

    final List<Component> tooltip = new ArrayList<>();

    public TooltipBuilder addTip(String text) {
        tooltip.add(new TextComponent(text));
        return this;
    }

    public TooltipBuilder addTip(String text, ChatFormatting formatting) {
        tooltip.add(new TextComponent(formatting + text));
        return this;
    }

    public TooltipBuilder addTranslatableTip(String key, ChatFormatting formatting) {
        tooltip.add(new TranslatableComponent(key).withStyle(formatting));
        return this;
    }
    public TooltipBuilder addTranslatableTip(String key) {
        tooltip.add(new TranslatableComponent(key));
        return this;
    }

    public TooltipBuilder applyStyle(ChatFormatting formatting) {
        TextComponent textComponent = new TextComponent(formatting + tooltip.remove(tooltip.size() - 1).getString());
        tooltip.add(textComponent);
        return this;
    }

    public TooltipBuilder appendText(String text) {
        MutableComponent textComponent = (MutableComponent) tooltip.remove(tooltip.size() - 1);
        textComponent.append(text);
        tooltip.add(textComponent);
        return this;
    }

    public TooltipBuilder appendText(String text, ChatFormatting formatting) {
        MutableComponent textComponent = (MutableComponent) tooltip.remove(tooltip.size() - 1);
        textComponent.append(formatting + text);
        tooltip.add(textComponent);
        return this;
    }

    public List<Component> build(){
        return tooltip.isEmpty() ? null : tooltip;
    }
}
