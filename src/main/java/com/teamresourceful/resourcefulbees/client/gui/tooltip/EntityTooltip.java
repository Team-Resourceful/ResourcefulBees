package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class EntityTooltip extends AbstractTooltip{

    Supplier<Entity> entitySupplier;
    boolean showNBT = false;

    public EntityTooltip(int x, int y, int hoverWidth, int hoverHeight, Entity entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = () -> entity;
    }

    public EntityTooltip(int x, int y, int hoverWidth, int hoverHeight, Supplier<Entity> entity) {
        super(x, y, hoverWidth, hoverHeight);
        this.entitySupplier = entity;
    }

    @Override
    public List<Component> getTooltip() {
        Entity entity = this.entitySupplier.get();
        List<Component> tooltips;
        if (entity instanceof CustomBeeEntity customBee) {
             tooltips = BeeTooltip.getTooltip(customBee.getBeeData(),true);
        }else {
            tooltips = new LinkedList<>();
            tooltips.add(entity.getDisplayName());
        }
        if (showNBT) {
            CompoundTag nbt = entity.saveWithoutId(new CompoundTag());
            tooltips.addAll(getNbtTooltips(nbt));
        }
        return tooltips;
    }

    @Override
    public List<Component> getAdvancedTooltip() {
        Entity entity = entitySupplier.get();
        List<Component> tooltips = getTooltip();
        if (entity.getType().getRegistryName() == null) return getTooltip();
        tooltips.add(new TextComponent(entity.getType().getRegistryName().toString()).withStyle(ChatFormatting.DARK_GRAY));
        return tooltips;
    }


    public EntityTooltip setDoNBT(boolean showNBT) {
        this.showNBT = showNBT;
        return this;
    }
}
