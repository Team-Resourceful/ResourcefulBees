package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    public EntityIngredient(EntityType<?> entityType, float rotation, Optional<CompoundTag> nbt) {
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

    public Component getDisplayName() {
        return Component.translatable(entityType.getDescriptionId());
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();

        if (entity != null) {
            if (entity instanceof CustomBeeEntity customBee) {
                tooltip.addAll(customBee.getCoreData().lore());
                tooltip.add(JeiTranslations.CLICK_INFO.withStyle(ChatFormatting.GOLD));
            }
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                tooltip.add(Component.literal(toString()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return tooltip;
    }

    public ResourceLocation getEntityId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    @Override
    public String toString() {
        return getEntityId().toString();
    }
}
