package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Supplier;

public class ItemTooltip extends AbstractTooltip {

    protected final Supplier<ItemStack> itemSupplier;

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = () -> item;
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = item;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return itemSupplier.get().getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }
}
