package com.teamresourceful.resourcefulbees.common.logistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Objects;

public record CourierFilter(
        CourierFilterMode mode,
        List<CourierFilterEntry> entries
) {

    public static final CourierFilter NONE = new CourierFilter(CourierFilterMode.NONE, List.of());

    public static final Codec<CourierFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CourierFilterMode.CODEC.fieldOf("mode").forGetter(CourierFilter::mode),
            CourierFilterEntry.CODEC.listOf().fieldOf("entries").forGetter(CourierFilter::entries)
    ).apply(instance, CourierFilter::new));

    public CourierFilter {
        Objects.requireNonNull(mode, "mode");
        entries = List.copyOf(entries);
    }


    public boolean allows(Item item) {
        return allowsMatch(
                entries.stream().anyMatch(entry -> entry.matches(item))
        );
    }

    public boolean allows(Fluid fluid) {
        return allowsMatch(
                entries.stream().anyMatch(entry -> entry.matches(fluid))
        );
    }

    private boolean allowsMatch(boolean matches) {
        return switch (mode) {
            case NONE -> true;
            case WHITELIST -> matches;
            case BLACKLIST -> !matches;
        };
    }

}