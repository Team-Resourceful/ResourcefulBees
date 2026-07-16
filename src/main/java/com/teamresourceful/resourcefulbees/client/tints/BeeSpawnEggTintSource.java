package com.teamresourceful.resourcefulbees.client.tints;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BeeSpawnEggTintSource() implements ItemTintSource {

    public static final MapCodec<BeeSpawnEggTintSource> CODEC = MapCodec.unit(BeeSpawnEggTintSource::new);

    @Override
    public int calculate(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (SpawnEggItem.getType(itemStack) instanceof CustomBeeEntityType<?> customBeeEntityType) {
            return customBeeEntityType.getData().getRenderData().colorData().primarySpawnEggColor().withAlpha(255).getValue();
        }
        return 0xffffffff;
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
