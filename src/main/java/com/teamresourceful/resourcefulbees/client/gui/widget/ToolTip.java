package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class ToolTip {
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;
    private Supplier<ITextComponent> text = null;
    private ItemStack item = null;
    private FluidStack fluid = null;
    private boolean showFluidAmount = true;
    private CustomBeeData bee = null;

    private static final TranslationTextComponent CREATOR_PREFIX = new TranslationTextComponent("tooltip.resourcefulbees.bee.creator");

    public ToolTip(int minX, int minY, int sizeX, int sizeY, Supplier<ITextComponent> text) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + sizeX;
        this.maxY = minY + sizeY;
        this.text = text;
    }

    public ToolTip(int minX, int minY, int sizeX, int sizeY, ItemStack item) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + sizeX;
        this.maxY = minY + sizeY;
        this.item = item;
    }

    public ToolTip(int minX, int minY, int sizeX, int sizeY, FluidStack fluid, boolean showFluidAmount) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + sizeX;
        this.maxY = minY + sizeY;
        this.fluid = fluid;
        this.showFluidAmount = showFluidAmount;
    }

    public ToolTip(int minX, int minY, int sizeX, int sizeY, CustomBeeData bee) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + sizeX;
        this.maxY = minY + sizeY;
        this.bee = bee;
    }

    public void draw(Screen screen, MatrixStack matrix, int mouseX, int mouseY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY) {
            if (bee != null) {
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(bee.getDisplayName());
                if (bee.getCoreData().getLore().isPresent()) {
                    tooltip.add(new StringTextComponent(bee.getCoreData().getLore().get()).withStyle(bee.getCoreData().getLoreColorStyle()));
                }
                if (bee.getCoreData().getCreator().isPresent()) {
                    tooltip.add(CREATOR_PREFIX.append(bee.getCoreData().getCreator().get()).withStyle(TextFormatting.GOLD));
                }
                if (Minecraft.getInstance().options.advancedItemTooltips) {
                    tooltip.add(new StringTextComponent(bee.getRegistryID().toString()).withStyle(TextFormatting.GRAY));
                }
                screen.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
            } else if (item != null) {
                screen.renderComponentTooltip(matrix, screen.getTooltipFromItem(item), mouseX, mouseY);
            } else if (text != null) {
                screen.renderTooltip(matrix, text.get(), mouseX, mouseY);
            } else if (fluid != null) {
                List<ITextComponent> tooltip = new ArrayList<>();
                tooltip.add(fluid.getDisplayName());
                if (fluid.getAmount() > 1) {
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    String amount = fluid.getAmount() < 500 || Screen.hasShiftDown() ? String.format("%,d", fluid.getAmount()) + " mb" : decimalFormat.format((float) fluid.getAmount() / 1000) + " B";
                    tooltip.add(new StringTextComponent(amount));
                }
                tooltip.add(new StringTextComponent(String.valueOf(fluid.getFluid().getRegistryName())).withStyle(TextFormatting.DARK_GRAY));
                screen.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
            }
        }
    }
}