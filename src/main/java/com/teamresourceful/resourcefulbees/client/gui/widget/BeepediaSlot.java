package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BeepediaSlot extends TooltipWidget {

    private final ITextComponent tooltip;
    private final ItemStack stack;

    public BeepediaSlot(int x, int y, ItemStack stack, ITextComponent tooltip) {
        super(x, y, 20, 20, tooltip);
        this.tooltip = tooltip;
        this.stack = stack;
    }

    public BeepediaSlot(int x, int y, ItemStack stack) {
        this(x, y, stack, new StringTextComponent(""));
    }
}
