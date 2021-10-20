package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class EntityTooltip extends AbstractTooltip{

    Supplier<Entity> entitySupplier;

    public EntityTooltip(int x, int y, int hoverWidth, int hoverHeight, Entity entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = () -> entity;
    }

    public EntityTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<Entity> entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = entity;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        Entity entity = this.entitySupplier.get();
        if (entity instanceof CustomBeeEntity) return BeeTooltip.getTooltip(((CustomBeeEntity) entity).getBeeData(),true);
        List<ITextComponent> tooltips = new LinkedList<>();
        tooltips.add(entity.getDisplayName());
        CompoundNBT nbt = entity.saveWithoutId(new CompoundNBT());
        tooltips.addAll(getNbtTooltips(nbt));
        return tooltips;
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        Entity entity = entitySupplier.get();
        if (entity instanceof CustomBeeEntity) return BeeTooltip.getAdvancedTooltip(((CustomBeeEntity) entity).getBeeData(),true);
        List<ITextComponent> tooltips = getTooltip();
        if (entity.getType().getRegistryName() == null) return getTooltip();
        tooltips.add(new StringTextComponent(entity.getType().getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
