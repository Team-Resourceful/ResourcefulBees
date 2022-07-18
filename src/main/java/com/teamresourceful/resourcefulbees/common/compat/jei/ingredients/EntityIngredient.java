package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
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
                tooltip.add(TranslationConstants.Jei.CLICK_INFO.withStyle(ChatFormatting.GOLD));
            }
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
                if (key != null) {
                    tooltip.add(Component.literal(key.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }
        return tooltip;
    }

    @Override
    public String toString() {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        return key != null ? key.toString() : entityType.toString();
    }
}
