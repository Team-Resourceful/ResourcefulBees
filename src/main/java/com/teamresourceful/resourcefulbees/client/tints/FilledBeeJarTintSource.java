package com.teamresourceful.resourcefulbees.client.tints;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record FilledBeeJarTintSource() implements ItemTintSource {

    public static final MapCodec<FilledBeeJarTintSource> CODEC = MapCodec.unit(FilledBeeJarTintSource::new);

    @Override
    public int calculate(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (BeeJarItem.isFilled(itemStack)) {
            return BeeJarItem.occupantFrom(itemStack).color();
        }

        return 0xffffffff;
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
