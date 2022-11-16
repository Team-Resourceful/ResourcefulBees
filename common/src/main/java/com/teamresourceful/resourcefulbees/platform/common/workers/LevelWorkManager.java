package com.teamresourceful.resourcefulbees.platform.common.workers;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

public final class LevelWorkManager {

    private LevelWorkManager() {
        throw new UtilityClassError();
    }

    @ExpectPlatform
    public static synchronized void addWork(LevelWorker worker) {
        throw new NotImplementedException();
    }
}
