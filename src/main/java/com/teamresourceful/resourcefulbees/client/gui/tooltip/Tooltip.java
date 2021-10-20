package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Tooltip extends AbstractTooltip {

    private Supplier<List<ITextComponent>> textList = null;
    private Supplier<ITextComponent> text = null;

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, String text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> Arrays.stream(text.split("\\r?\\n")).map(StringTextComponent::new).collect(Collectors.toList());
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, ITextComponent text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> Collections.singletonList(text);
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, List<ITextComponent> text) {
        super(x, y, hoverWidth, hoverHeight);
        this.textList = () -> text;
    }

    public Tooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ITextComponent> text) {
        super(x, y, hoverWidth, hoverHeight);
        this.text = text;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        if (textList != null) {
            return textList.get();
        }else if (text != null) {
            return Collections.singletonList(text.get());
        }
        return new ArrayList<>();
    }
}
