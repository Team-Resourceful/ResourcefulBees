package com.teamresourceful.resourcefulbees.common.world.workers;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LevelWorkManager {

    private static final List<LevelWorker> WORKERS = new ArrayList<>();
    private static final List<LevelWorker> PENDING_WORKERS = new ArrayList<>();

    private static boolean ticking;

    private LevelWorkManager() {
    }

    public static void addWork(LevelWorker worker) {
        if (ticking) {
            PENDING_WORKERS.add(worker);
        } else {
            WORKERS.add(worker);
        }
    }

    public static void tick(ServerLevel level) {
        ticking = true;

        try {
            Iterator<LevelWorker> iterator = WORKERS.iterator();

            while (iterator.hasNext()) {
                LevelWorker worker = iterator.next();

                if (worker.level() != level) {
                    continue;
                }

                if (!worker.work()) {
                    iterator.remove();
                }
            }
        } finally {
            ticking = false;
        }

        if (!PENDING_WORKERS.isEmpty()) {
            WORKERS.addAll(PENDING_WORKERS);
            PENDING_WORKERS.clear();
        }
    }

    public static void clear(ServerLevel level) {
        WORKERS.removeIf(worker -> worker.level() == level);
        PENDING_WORKERS.removeIf(worker -> worker.level() == level);
    }
}