package com.teamresourceful.resourcefulbees.platform.common.workers;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;

public final class LevelWorkManager {

    private LevelWorkManager() {
        throw new UtilityClassError();
    }

    @ExpectPlatform
    public static synchronized void addWork(LevelWorker worker) {
        throw new NotImplementedError();
    }
}
