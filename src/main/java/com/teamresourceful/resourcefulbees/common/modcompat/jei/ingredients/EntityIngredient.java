package com.teamresourceful.resourcefulbees.common.modcompat.jei.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.JeiTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record EntityIngredient(EntityType<?> entityType, float rotation, @Nullable Entity entity) {

    public static final Codec<EntityIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityType.CODEC.fieldOf("entity_type").forGetter(EntityIngredient::entityType),
            Codec.FLOAT.optionalFieldOf("rotation", BeeConstants.ENTITY_DISPLAY_ROTATION).forGetter(EntityIngredient::rotation)
    ).apply(instance, EntityIngredient::new));

    public EntityIngredient(EntityType<?> entityType, float rotation) {
        Minecraft minecraft = Minecraft.getInstance();
        this(entityType, rotation, minecraft.level == null ? null : entityType.create(minecraft.level, EntitySpawnReason.COMMAND));
    }

    public static EntityIngredient of(EntityType<?> entityType) {
        return new EntityIngredient(entityType, BeeConstants.ENTITY_DISPLAY_ROTATION);
    }

    public Component getDisplayName() {
        return entityType.getDescription();
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();

        if (entity instanceof CustomBeeEntity customBee) {
            tooltip.addAll(customBee.getCoreData().lore());
            tooltip.add(JeiTranslations.CLICK_INFO.withStyle(ChatFormatting.GOLD));
        }

        if (Minecraft.getInstance().options.advancedItemTooltips) {
            tooltip.add(Component.literal(toString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        return tooltip;
    }

    public Identifier getEntityId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }

    @Override
    public @NonNull String toString() {
        return getEntityId().toString();
    }
}