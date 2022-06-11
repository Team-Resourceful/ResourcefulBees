package com.teamresourceful.resourcefulbees.common.registry.dynamic;

import com.teamresourceful.resourcefulbees.api.spawndata.SpawnData;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import net.minecraft.util.InclusiveRange;

import java.util.HashMap;

public class SpawnerRegistry extends HashMap<String, SpawnData> {

    public static final SpawnerRegistry INSTANCE = new SpawnerRegistry();
    private static final SpawnData DEFAULT_DATA = new SpawnData(LightLevel.ANY, new InclusiveRange<>(-512, 512));

    private SpawnerRegistry() {
        super();
    }

    public static SpawnData getData(String bee) {
        return INSTANCE.getOrDefault(bee, DEFAULT_DATA);
    }
}
