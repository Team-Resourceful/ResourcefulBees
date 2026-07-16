package com.teamresourceful.resourcefulbees.client.tints;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record HoneycombTintSource() implements ItemTintSource {

    public static final MapCodec<HoneycombTintSource> CODEC = MapCodec.unit(HoneycombTintSource::new);

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (itemStack.getItem() instanceof CustomHoneycombItem honeycombItem) {
            return honeycombItem.color();
        }

        return 0xffffffff;
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
