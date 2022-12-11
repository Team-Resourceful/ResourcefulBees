package com.teamresourceful.resourcefulbees.platform.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

public class ModUtils {

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
