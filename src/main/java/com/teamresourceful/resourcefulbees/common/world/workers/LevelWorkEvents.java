package com.teamresourceful.resourcefulbees.common.world.workers;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class LevelWorkEvents {

    private LevelWorkEvents() throws UtilityClassException {
        throw new UtilityClassException();
    }


    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            LevelWorkManager.tick(level);
        }
    }
}