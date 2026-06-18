package com.teamresourceful.resourcefulbees.common.world.workers;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import dev.architectury.injectables.annotations.ExpectPlatform;

public final class LevelWorkManager {

    private LevelWorkManager() throws UtilityClassException {
        throw new UtilityClassException();
    }

    //todo see https://github.com/neoforged/NeoForge/issues/2436
    public static synchronized void addWork(LevelWorker worker) {
        WorldWorkerManager.addWorker(new WorkerWorker(worker));
    }

    private record WorkerWorker(LevelWorker worker) implements WorldWorkerManager.IWorker {

        @Override
        public boolean hasWork() {
            return worker.canWork();
        }

        @Override
        public boolean doWork() {
            return worker.work();
        }
    }
}
