
package com.teamresourceful.resourcefulbees.api.registry;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.HoneycombDefinition;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HoneycombRegistration {

    private static final Map<Identifier, HoneycombDefinition> DEFINITIONS = new LinkedHashMap<>();

    private static boolean closed;

    private HoneycombRegistration() {}

    public static synchronized void register(HoneycombDefinition definition) {
        if (closed) {
            throw new IllegalStateException(
                    "Honeycomb registration is closed"
            );
        }

        if (DEFINITIONS.putIfAbsent(
                definition.name(), definition
        ) != null) {
            throw new IllegalArgumentException(
                    "Duplicate honeycomb: " + definition.name()
            );
        }
    }

    public static synchronized List<HoneycombDefinition> drain() {
        closed = true;
        return List.copyOf(DEFINITIONS.values());
    }
}
