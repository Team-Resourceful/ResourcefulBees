package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FluidTooltip extends AbstractTooltip {

    private final Supplier<FluidStack> fluidSupplier;
    private final boolean showFluid;

    public FluidTooltip(int x, int y, int hoverWidth, int hoverHeight, FluidStack fluid, boolean showFluid) {
        super(x, y, hoverWidth, hoverHeight);
        this.fluidSupplier = () -> fluid;
        this.showFluid = showFluid;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        FluidStack fluid = this.fluidSupplier.get();
        List<ITextComponent> tooltips = new ArrayList<>();
        tooltips.add(fluid.getDisplayName());
        if (fluid.getAmount() > 1 || showFluid) {
            DecimalFormat decimalFormat = new DecimalFormat("##0.0");
            String amount = fluid.getAmount() < 500 || Screen.hasShiftDown() ? String.format("%,d", fluid.getAmount()) + " mb" : decimalFormat.format((float) fluid.getAmount() / 1000) + " B";
            tooltips.add(new StringTextComponent(amount));
        }
        tooltips.add(new StringTextComponent(String.valueOf(fluid.getFluid().getRegistryName())).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        FluidStack fluid = this.fluidSupplier.get();
        List<ITextComponent> tooltips = getTooltip();
        if (fluid.getFluid().getRegistryName() == null) return getTooltip();
        tooltips.add(new StringTextComponent(fluid.getFluid().getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
