package com.teamresourceful.resourcefulbees.platform.common.workers;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import dev.architectury.injectables.annotations.ExpectPlatform;

public final class LevelWorkManager {

    private LevelWorkManager() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @ExpectPlatform
    public static synchronized void addWork(LevelWorker worker) {
        throw new NotImplementedException();
    }
}
