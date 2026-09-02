package com.teamresourceful.resourcefulbees.common.world.workers;

import net.minecraft.server.level.ServerLevel;

public interface LevelWorker {

    ServerLevel level();

    /**
     * Performs one unit of work.
     *
     * @return true while this worker should remain scheduled,
     *         false when it is finished and should be removed
     */
    boolean work();
}