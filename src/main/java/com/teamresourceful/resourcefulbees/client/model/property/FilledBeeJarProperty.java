package com.teamresourceful.resourcefulbees.client.model.property;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record FilledBeeJarProperty() implements ConditionalItemModelProperty {

    public static final MapCodec<FilledBeeJarProperty> MAP_CODEC =  MapCodec.unit(new FilledBeeJarProperty());

    @Override
    public @NonNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, @NonNull ItemDisplayContext displayContext) {
        return BeeJarItem.isFilled(itemStack);
    }
}
