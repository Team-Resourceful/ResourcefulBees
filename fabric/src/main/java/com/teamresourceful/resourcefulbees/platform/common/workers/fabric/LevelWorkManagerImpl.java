package com.teamresourceful.resourcefulbees.platform.common.workers.fabric;

import com.teamresourceful.resourcefulbees.platform.common.workers.LevelWorker;

public class LevelWorkManagerImpl {
    public static void addWork(LevelWorker worker) {
        FabricLevelWorker.add(worker);
    }
}
