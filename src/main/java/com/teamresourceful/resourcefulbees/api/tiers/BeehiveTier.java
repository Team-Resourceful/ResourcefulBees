package com.teamresourceful.resourcefulbees.api.tiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record BeehiveTier(Identifier id, int maxBees, int maxCombs, double timeModifier, Supplier<Collection<Item>> displayItems) {

    private static final Map<Identifier, BeehiveTier> TIERS = new HashMap<>();
    public static final Codec<BeehiveTier> CODEC = Identifier.CODEC.comapFlatMap(BeehiveTier::get, BeehiveTier::id);

    public BeehiveTier {
        if (TIERS.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Beehive Tier: " + id);
        }
        TIERS.put(id, this);
    }

    public String getTimeModificationAsPercent() {
        return String.format("%+d",(100 - (int)(timeModifier() * 100)) * -1);
    }

    public Collection<Item> getDisplayItems() {
        return displayItems.get();
    }

    public static DataResult<BeehiveTier> get(Identifier id) {
        if (TIERS.containsKey(id)) {
            return DataResult.success(TIERS.get(id));
        }
        return DataResult.error(() -> "Unknown Beehive Tier: " + id);
    }

    public static BeehiveTier getOrThrow(Identifier id) {
        return get(id).getOrThrow();
    }

    public static Collection<BeehiveTier> values() {
        return TIERS.values();
    }

    public static class Builder {
        private int maxBees;
        private int maxCombs;
        private double timeModifier;
        private Supplier<Collection<Item>> displayItems;

        public Builder maxBees(int maxBees) {
            this.maxBees = maxBees;
            return this;
        }

        public Builder maxCombs(int maxCombs) {
            this.maxCombs = maxCombs;
            return this;
        }

        public Builder timeModifier(double timeModifier) {
            this.timeModifier = timeModifier;
            return this;
        }

        public Builder displayItems(Supplier<Collection<Item>> displayItems) {
            this.displayItems = displayItems;
            return this;
        }

        public BeehiveTier build(Identifier id) {
            return new BeehiveTier(id, maxBees, maxCombs, timeModifier, displayItems);
        }
    }
}
