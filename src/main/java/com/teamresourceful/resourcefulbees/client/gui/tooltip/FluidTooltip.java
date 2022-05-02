package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class FluidTooltip extends AbstractTooltip {

    private final Supplier<FluidStack> fluidSupplier;
    private final boolean showFluid;

    public FluidTooltip(int x, int y, int hoverWidth, int hoverHeight, FluidStack fluid, boolean showFluid) {
        super(x, y, hoverWidth, hoverHeight);
        this.fluidSupplier = () -> fluid;
        this.showFluid = showFluid;
    }

    @Override
    public List<Component> getTooltip() {
        FluidStack fluid = this.fluidSupplier.get();
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(fluid.getDisplayName());
        if (fluid.getAmount() > 1 || showFluid) {
            String amount = fluid.getAmount() < 500 || Screen.hasShiftDown() ? String.format("%,d", fluid.getAmount()) + " mb" : ModConstants.DECIMAL_FORMAT.format((float) fluid.getAmount() / 1000) + " B";
            tooltips.add(new TextComponent(amount));
        }
        tooltips.add(new TextComponent(String.valueOf(fluid.getFluid().getRegistryName())).withStyle(ChatFormatting.DARK_GRAY));
        return tooltips;
    }

    @Override
    public List<Component> getAdvancedTooltip() {
        FluidStack fluid = this.fluidSupplier.get();
        List<Component> tooltips = getTooltip();
        if (fluid.getFluid().getRegistryName() == null) return getTooltip();
        tooltips.add(new TextComponent(fluid.getFluid().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY));
        return tooltips;
    }
}
