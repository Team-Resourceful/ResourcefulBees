package com.teamresourceful.resourcefulbees.common.lib.records;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.*;
import java.util.function.Supplier;

public record HiveType(Identifier id, String type, Supplier<BlockBehaviour.Properties> properties, List<Supplier<? extends Block>> hiveBreakBlocks) {

    public static final Codec<HiveType> CODEC = Identifier.CODEC.comapFlatMap(HiveType::get, HiveType::id);
    private static final Map<Identifier, HiveType> HIVE_TYPES = new HashMap<>();
    private static final Map<HiveType, Supplier<Block>> TIER_ONE_NESTS = new HashMap<>();



    public HiveType {
        if (HIVE_TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Hive Type: " + id);
        }
        hiveBreakBlocks = List.copyOf(hiveBreakBlocks);
        HIVE_TYPES.put(id, this);
    }

    public static Collection<HiveType> values() {
        return HIVE_TYPES.values();
    }

    public static DataResult<HiveType> get(Identifier id) {
        HiveType hiveType = HIVE_TYPES.get(id);

        if (hiveType != null) {
            return DataResult.success(hiveType);
        }

        return DataResult.error(
                () -> "Unknown Hive Type: " + id
        );
    }

    public void cacheTierOneNest(Supplier<Block> nest) {
        if (TIER_ONE_NESTS.putIfAbsent(this, nest) != null) {
            throw new IllegalStateException(
                    "Tier One nest already registered for hive type: " + id
            );
        }
    }

    public Block tierOneNest() {
        Supplier<Block> nest = TIER_ONE_NESTS.get(this);

        if (nest == null) {
            throw new IllegalStateException(
                    "No tier one nest registered for hive type: " + id
            );
        }

        return nest.get();
    }

    public static HiveType getOrThrow(Identifier id) {
        return get(id).getOrThrow();
    }

    public static class Builder {
        String type;
        Supplier<BlockBehaviour.Properties> properties;
        List<Supplier<? extends Block>> hiveBreakBlocks = new ArrayList<>();

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder properties(Supplier<BlockBehaviour.Properties> properties) {
            this.properties = properties;
            return this;
        }

        @SafeVarargs
        public final Builder hiveBreakBlocks(
                Supplier<? extends Block>... blocks
        ) {
            Collections.addAll(hiveBreakBlocks, blocks);
            return this;
        }

        public HiveType build(Identifier id) {
            return new HiveType(
                    id,
                    type,
                    properties,
                    hiveBreakBlocks
            );
        }
    }
}
