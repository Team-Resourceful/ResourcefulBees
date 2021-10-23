package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.entity.EntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class EntityTypeToolTip extends AbstractTooltip {

    private final Supplier<EntityType<?>> entitySupplier;

    public EntityTypeToolTip(int x, int y, int hoverWidth, int hoverHeight, EntityType<?> entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = () -> entity;
    }

    public EntityTypeToolTip(int x, int y, int hoverWidth, int hoverHeight, Supplier<EntityType<?>> entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = entity;
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return Collections.singletonList(entitySupplier.get().getDescription());
    }

    @Override
    public List<ITextComponent> getAdvancedTooltip() {
        EntityType<?> entity = this.entitySupplier.get();
        List<ITextComponent> tooltips = getTooltip();
        if (entity.getRegistryName() == null) return getTooltip();
        tooltips.add(new StringTextComponent(entity.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
        return tooltips;
    }
}
