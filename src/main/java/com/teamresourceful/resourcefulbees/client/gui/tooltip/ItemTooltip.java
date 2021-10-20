package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ItemTooltip extends AbstractTooltip {

    private final ItemStack item;

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item) {
        super(x, y, hoverWidth, hoverHeight);
        this.item = item;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return item.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }
}
