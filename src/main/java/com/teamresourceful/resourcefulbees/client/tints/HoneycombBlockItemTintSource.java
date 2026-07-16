package com.teamresourceful.resourcefulbees.client.tints;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.blocks.HoneycombBlock;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record HoneycombBlockItemTintSource() implements ItemTintSource {

    public static final MapCodec<HoneycombBlockItemTintSource> CODEC = MapCodec.unit(HoneycombBlockItemTintSource::new);

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof HoneycombBlock honeycombBlock) {
            return honeycombBlock.color();
        }

        return 0xffffffff;
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}