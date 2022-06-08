package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class BeeTooltip extends AbstractTooltip {

    Supplier<CustomBeeData> beeDataSupplier;
    private boolean showName = true;

    public BeeTooltip(int x, int y, int hoverWidth, int hoverHeight, CustomBeeData beeData) {
        super(x, y, hoverWidth, hoverHeight);
        this.beeDataSupplier = () -> beeData;
    }

    public BeeTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<CustomBeeData> beeData) {
        super(x, y, hoverWidth, hoverHeight);
        this.beeDataSupplier = beeData;
    }

    public void toggleShowName() {
        this.showName = !showName;
    }

    @Override
    public List<Component> getTooltip() {
        return getTooltip(this.beeDataSupplier.get(), showName);
    }

    @Override
    public List<Component> getAdvancedTooltip() {
        return getAdvancedTooltip(this.beeDataSupplier.get(), showName);
    }

    public static List<Component> getTooltip(CustomBeeData beeData, boolean showName) {
        List<Component> tooltips = new LinkedList<>();
        if (showName) tooltips.add(beeData.displayName());
        tooltips.addAll(getBeeLore(beeData.coreData()));
        return tooltips;
    }

    public static List<Component> getAdvancedTooltip(CustomBeeData beeData, boolean showName) {
        List<Component> tooltips = getTooltip(beeData, showName);
        if (beeData.registryID() == null) return getTooltip(beeData, showName);
        tooltips.add(new TextComponent(beeData.registryID().toString()).withStyle(ChatFormatting.DARK_GRAY));
        return tooltips;
    }
}
