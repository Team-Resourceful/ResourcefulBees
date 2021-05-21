package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
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
    private Supplier<Component> text = null;
    private ItemStack item = null;
    private FluidStack fluid = null;
    private boolean showFluidAmount = true;
    private CustomBeeData bee = null;

    private static final TranslatableComponent CREATOR_PREFIX = new TranslatableComponent("tooltip.resourcefulbees.bee.creator");

    public ToolTip(int minX, int minY, int sizeX, int sizeY, Supplier<Component> text) {
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

    public void draw(Screen screen, PoseStack matrix, int mouseX, int mouseY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY) {
            if (bee != null) {
                List<Component> tooltip = new LinkedList<>();
                tooltip.add(bee.getTranslation());
                if (bee.getCoreData().getLore().isPresent()) {
                    tooltip.add(new TextComponent(bee.getCoreData().getLore().get()).withStyle(bee.getCoreData().getLoreColorStyle()));
                }
                if (bee.getCoreData().getCreator().isPresent()) {
                    tooltip.add(CREATOR_PREFIX.append(bee.getCoreData().getCreator().get()).withStyle(ChatFormatting.GOLD));
                }
                if (Minecraft.getInstance().options.advancedItemTooltips) {
                    tooltip.add(new TextComponent(bee.getRegistryID().toString()).withStyle(ChatFormatting.GRAY));
                }
                screen.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
            } else if (item != null) {
                screen.renderComponentTooltip(matrix, screen.getTooltipFromItem(item), mouseX, mouseY);
            } else if (text != null) {
                screen.renderTooltip(matrix, text.get(), mouseX, mouseY);
            } else if (fluid != null) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(fluid.getDisplayName());
                if (fluid.getAmount() > 1) {
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    String amount = fluid.getAmount() < 500 || BeeInfoUtils.isShiftPressed() ? String.format("%,d", fluid.getAmount()) + " mb" : decimalFormat.format((float) fluid.getAmount() / 1000) + " B";
                    tooltip.add(new TextComponent(amount));
                }
                tooltip.add(new TextComponent(fluid.getFluid().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY));
                screen.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
            }
        }
    }
}