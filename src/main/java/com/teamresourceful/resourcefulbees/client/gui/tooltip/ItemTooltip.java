package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ItemTooltip extends AbstractTooltip {

    protected final Supplier<ItemStack> itemSupplier;
    protected final Supplier<CompoundNBT> nbtSupplier;

    private boolean doNBT = false;

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item, CompoundNBT nbt) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = () -> item;
        this.nbtSupplier = () -> nbt;
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item, Supplier<CompoundNBT> nbtSupplier) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = item;
        this.nbtSupplier = nbtSupplier;
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item) {
        this(x, y, hoverWidth, hoverHeight, item, CompoundNBT::new);
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item) {
        this(x, y, hoverWidth, hoverHeight, item, new CompoundNBT());
    }

    public ItemTooltip setDoNBT(boolean doNBT) {
        this.doNBT = doNBT;
        return this;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltips = itemSupplier.get().getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        if (doNBT) {
            CompoundNBT nbt = nbtSupplier.get();
            tooltips.addAll(getNbtTooltips(nbt));
        }
        return tooltips;
    }
}
