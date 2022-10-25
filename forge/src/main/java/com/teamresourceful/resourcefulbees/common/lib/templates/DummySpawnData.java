package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.spawndata.SpawnData;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import net.minecraft.util.InclusiveRange;

import java.util.Optional;

public class DummySpawnData {

    public static final SpawnData DUMMY_SPAWN_DATA = new SpawnData(LightLevel.ANY, Optional.of(new InclusiveRange<>(12, 45)));
}
