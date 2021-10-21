package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityIngredient {

    @Nullable
    private final Entity entity;
    private final EntityType<?> entityType;
    private final float rotation;

    public EntityIngredient(EntityType<?> entityType, float rotation){
        this(entityType, rotation, Optional.empty());
    }

    public EntityIngredient(EntityType<?> entityType, float rotation, Optional<CompoundNBT> nbt) {
        this.rotation = rotation;
        this.entityType = entityType;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            entity = this.entityType.create(mc.level);
            if (entity != null) nbt.ifPresent(entity::load);
        }else {
            entity = null;
        }
    }

    public float getRotation() {
        return rotation;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(entityType.getDescriptionId());
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();

        if (entity != null) {
            if (entity instanceof CustomBeeEntity) {
                tooltip.addAll(((CustomBeeEntity) entity).getCoreData().getLore());
                String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
                for (String s : desc.split("\\r?\\n")) tooltip.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
            }
            if (Minecraft.getInstance().options.advancedItemTooltips && entityType.getRegistryName() != null) {
                tooltip.add(new StringTextComponent(entityType.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
            }
        }
        return tooltip;
    }

    @Override
    public String toString() {
        return entityType.getRegistryName() != null ? entityType.getRegistryName().toString() : entityType.toString();
    }
}
