package com.teamresourceful.resourcefulbees.client.tints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record PollenTintSource(int layer) implements ItemTintSource {

    public static final MapCodec<PollenTintSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("layer").forGetter(PollenTintSource::layer)
    ).apply(instance, PollenTintSource::new));

    @Override
    public int calculate(@NonNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        var recipe = stack.get(ModDataComponents.POLLEN_RECIPE);

        if (recipe == null) {
            return 0xFFFFFFFF;
        }

        return switch (layer) {
            case 0 -> recipe.baseColor().getOpaqueValue();
            case 1 -> recipe.topColor().getOpaqueValue();
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    public @NonNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
