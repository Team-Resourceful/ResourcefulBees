package com.teamresourceful.resourcefulbees.common.lib.records;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record HiveType(Identifier id, String type, Supplier<BlockBehaviour.Properties> properties) {

    private static final Map<Identifier, HiveType> HIVE_TYPES = new HashMap<>();
    public static final Codec<HiveType> CODEC = Identifier.CODEC.comapFlatMap(HiveType::get, HiveType::id);


    public HiveType {
        if (HIVE_TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate Hive Type: " + id);
        }
        HIVE_TYPES.put(id, this);
    }

    public static Collection<HiveType> values() {
        return HIVE_TYPES.values();
    }

    public static DataResult<HiveType> get(Identifier id) {
        if (HIVE_TYPES.containsKey(id)) {
            return DataResult.success(HIVE_TYPES.get(id));
        }
        return DataResult.error(() -> "Unknown Hive Type: " + id);
    }

    public static HiveType getOrThrow(Identifier id) {
        return get(id).getOrThrow();
    }

    public static class Builder {
        String type;
        Supplier<BlockBehaviour.Properties> properties;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder properties(Supplier<BlockBehaviour.Properties> properties) {
            this.properties = properties;
            return this;
        }

        public HiveType build(Identifier id) {
            return new HiveType(id, type, properties);
        }
    }
}
