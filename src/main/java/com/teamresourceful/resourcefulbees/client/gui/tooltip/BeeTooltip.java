package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;

public class BeeTooltip extends AbstractTooltip {

    CustomBeeData beeData;

    public BeeTooltip(int x, int y, int hoverWidth, int hoverHeight, CustomBeeData beeData) {
        super(x, y, hoverWidth, hoverHeight);
        this.beeData = beeData;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltips = new LinkedList<>();
        tooltips.add(beeData.getDisplayName());
        tooltips.addAll(BeeInfoUtils.getBeeLore(beeData.getCoreData()));
        return tooltips;
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        List<ITextComponent> tooltips = getTooltip();
        if (beeData.getRegistryID() == null) return getTooltip();
        tooltips.add(new StringTextComponent(beeData.getRegistryID().getPath()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
