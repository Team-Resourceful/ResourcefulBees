package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Supplier;

public class NbtItemTooltip extends ItemTooltip {

    Supplier<CompoundNBT> nbtSupplier;

    public NbtItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item, CompoundNBT nbt) {
        super(x, y, hoverWidth, hoverHeight, item);
        this.nbtSupplier = () -> nbt;
    }

    public NbtItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item) {
        super(x, y, hoverWidth, hoverHeight, item);
        this.nbtSupplier = item::getTag;
    }

    public NbtItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item, Supplier<CompoundNBT> nbtSupplier) {
        super(x, y, hoverWidth, hoverHeight, item);
        this.nbtSupplier = nbtSupplier;
    }

    public NbtItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item) {
        super(x, y, hoverWidth, hoverHeight, item);
        this.nbtSupplier = () -> this.itemSupplier.get().getTag();
    }

    @Override
    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltips = super.getTooltip();
        CompoundNBT nbt = nbtSupplier.get();
        tooltips.addAll(getNbtTooltips(nbt));
        return tooltips;
    }
}
