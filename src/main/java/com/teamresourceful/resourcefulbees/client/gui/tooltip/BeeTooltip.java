package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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
    public List<ITextComponent> getTooltip() {
        return getTooltip(this.beeDataSupplier.get(), showName);
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        return getAdvancedTooltip(this.beeDataSupplier.get(), showName);
    }

    public static List<ITextComponent> getTooltip(CustomBeeData beeData, boolean showName) {
        List<ITextComponent> tooltips = new LinkedList<>();
        if (showName) tooltips.add(beeData.getDisplayName());
        tooltips.addAll(getBeeLore(beeData.getCoreData()));
        return tooltips;
    }

    public static List<ITextComponent> getAdvancedTooltip(CustomBeeData beeData, boolean showName) {
        List<ITextComponent> tooltips = getTooltip(beeData, showName);
        if (beeData.getRegistryID() == null) return getTooltip(beeData, showName);
        tooltips.add(new StringTextComponent(beeData.getRegistryID().toString()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
