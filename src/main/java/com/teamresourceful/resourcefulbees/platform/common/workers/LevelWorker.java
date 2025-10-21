package com.teamresourceful.resourcefulbees.platform.common.workers;

/**
 * This is like the forge world worker.
 */
public interface LevelWorker {

    boolean canWork();

    boolean work();

    default void addWork() {
        LevelWorkManager.addWork(this);
    }
}
