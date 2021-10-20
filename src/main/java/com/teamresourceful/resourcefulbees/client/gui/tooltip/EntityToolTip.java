package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.entity.EntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

public class EntityToolTip extends AbstractTooltip {

    EntityType<?> entity;

    public EntityToolTip(int x, int y, int hoverWidth, int hoverHeight, EntityType<?> entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entity = entity;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return Collections.singletonList(entity.getDescription());
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        List<ITextComponent> tooltips = getTooltip();
        if (entity.getRegistryName() == null) return getTooltip();
        tooltips.add(new StringTextComponent(entity.getRegistryName().getPath()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
