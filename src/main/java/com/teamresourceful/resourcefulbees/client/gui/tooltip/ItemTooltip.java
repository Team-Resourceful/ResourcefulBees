package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ItemTooltip extends AbstractTooltip {

    protected final Supplier<ItemStack> itemSupplier;
    protected final Supplier<CompoundTag> nbtSupplier;

    private boolean doNBT = false;

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item, CompoundTag nbt) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = () -> item;
        this.nbtSupplier = () -> nbt;
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item, Supplier<CompoundTag> nbtSupplier) {
        super(x, y, hoverWidth, hoverHeight);
        this.itemSupplier = item;
        this.nbtSupplier = nbtSupplier;
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<ItemStack> item) {
        this(x, y, hoverWidth, hoverHeight, item, CompoundTag::new);
    }

    public ItemTooltip(int x, int y, int hoverWidth, int hoverHeight, ItemStack item) {
        this(x, y, hoverWidth, hoverHeight, item, new CompoundTag());
    }

    public ItemTooltip setDoNBT(boolean doNBT) {
        this.doNBT = doNBT;
        return this;
    }

    @Override
    public List<Component> getTooltip() {
        List<Component> tooltips = itemSupplier.get().getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        if (doNBT) {
            CompoundTag nbt = nbtSupplier.get();
            tooltips.addAll(getNbtTooltips(nbt));
        }
        return tooltips;
    }
}
