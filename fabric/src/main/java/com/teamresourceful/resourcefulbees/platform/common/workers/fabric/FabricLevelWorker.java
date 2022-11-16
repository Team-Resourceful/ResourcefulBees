package com.teamresourceful.resourcefulbees.platform.common.workers.fabric;

import com.teamresourceful.resourcefulbees.platform.common.workers.LevelWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a copy of forges WorldWorkerManager but for fabric
 */
public class FabricLevelWorker {

    private static final List<LevelWorker> WORKERS = new ArrayList<>();
    private static long startTime = -1;
    private static int index = 0;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void tick() {
        index = 0;
        LevelWorker worker = getNext();
        if (worker != null) {

            long time = 50 - (System.currentTimeMillis() - startTime);
            if (time < 10) time = 10;
            time += System.currentTimeMillis();

            while (worker != null && System.currentTimeMillis() < time) {
                boolean canRun = worker.work();
                if (!worker.canWork()) {
                    remove(worker);
                    worker = getNext();
                } else if (!canRun) {
                    worker = getNext();
                }
            }
        }
    }

    public static synchronized void add(LevelWorker worker) {
        WORKERS.add(worker);
    }

    private static synchronized LevelWorker getNext() {
        return WORKERS.size() > index ? WORKERS.get(index++) : null;
    }

    private static synchronized void remove(LevelWorker worker) {
        WORKERS.remove(worker);
        index--;
    }

    public static synchronized void clear() {
        WORKERS.clear();
    }
}
