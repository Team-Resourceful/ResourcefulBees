package com.teamresourceful.resourcefulbees.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class EntityIngredient {

    private final Entity entity;
    private final CustomBeeData beeData;
    private final float rotation;
    private static final TranslationTextComponent CREATOR_PREFIX = new TranslationTextComponent("tooltip.resourcefulbees.bee.creator");

    public EntityIngredient(CustomBeeData beeData, float rotation) {
        this.beeData = beeData;
        this.rotation = rotation;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            EntityType<?> entityType = beeData.getEntityType();
            entity = entityType.create(mc.level);
        }else {
            entity = null;
        }
    }

    public CustomBeeData getBeeData() {
        return beeData;
    }

    public float getRotation() {
        return rotation;
    }

    public Entity getEntity() {
        return entity;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("entity.resourcefulbees." + beeData.getCoreData().getName() + "_bee");
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (beeData.getCoreData().getLore().isPresent()) {
            String lore = beeData.getCoreData().getLore().get();
            String[] loreTooltip = lore.split("\\r?\\n");
            for (String s: loreTooltip) {
                tooltip.add(new StringTextComponent(s).withStyle(beeData.getCoreData().getLoreColorStyle()));
            }
        }
        if (beeData.getCoreData().getCreator().isPresent()) {
            tooltip.add(CREATOR_PREFIX.copy().append(beeData.getCoreData().getCreator().get()).withStyle(TextFormatting.GRAY));
        }
        String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
        String[] descTooltip = desc.split("\\r?\\n");
        for (String s : descTooltip) {
            tooltip.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(new StringTextComponent(beeData.getRegistryID().toString()).withStyle(TextFormatting.DARK_GRAY));
        }
        return tooltip;
    }

    @Override
    public String toString() {
        return beeData.getCoreData().getName();
    }
}
