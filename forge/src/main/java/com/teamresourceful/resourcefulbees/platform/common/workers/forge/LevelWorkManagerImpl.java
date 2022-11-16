package com.teamresourceful.resourcefulbees.platform.common.workers.forge;

import com.teamresourceful.resourcefulbees.platform.common.workers.LevelWorker;
import net.minecraftforge.common.WorldWorkerManager;

public class LevelWorkManagerImpl {
    public static void addWork(LevelWorker worker) {
        WorldWorkerManager.addWorker(new WorldWorkerManager.IWorker() {
            @Override
            public boolean hasWork() {
                return worker.canWork();
            }

            @Override
            public boolean doWork() {
                return worker.work();
            }
        });
    }
}
