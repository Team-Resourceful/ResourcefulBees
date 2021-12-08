package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class Tooltip extends AbstractTooltip {

    private Supplier<List<Component>> textList = null;
    private Supplier<Component> text = null;

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, String text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> Arrays.stream(text.split("\\r?\\n")).map(TextComponent::new).collect(Collectors.toList());
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, Component text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> Collections.singletonList(text);
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, List<Component> text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> text;
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<Component> text) {
        super(x, y, hoverWidth, hoverHeight);
        this.text = text;
    }

    @Override
    public List<Component> getTooltip() {
        if (textList != null) {
            return textList.get();
        }else if (text != null) {
            return Collections.singletonList(text.get());
        }
        return new ArrayList<>();
    }
}
