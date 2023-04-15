package com.teamresourceful.resourcefulbees.platform.common.entity;

import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.util.RandomSource;

public class DespawnHandler {

    @ExpectPlatform
    public static void checkDespawn(CustomBeeEntity bee, RandomSource random, boolean despawnInPeaceful) {
        throw new NotImplementedError();
    }
}
