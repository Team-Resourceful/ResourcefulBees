package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
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
    public List<Component> getTooltip() {
        return Collections.singletonList(entitySupplier.get().getDescription());
    }

    @Override
    public List<Component> getAdvancedTooltip() {
        EntityType<?> entity = this.entitySupplier.get();
        List<Component> tooltips = getTooltip();
        if (entity.getRegistryName() == null) return getTooltip();
        tooltips.add(new TextComponent(entity.getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY));
        return tooltips;
    }
}
