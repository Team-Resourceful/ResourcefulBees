package com.teamresourceful.resourcefulbees.common.world.workers;

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
