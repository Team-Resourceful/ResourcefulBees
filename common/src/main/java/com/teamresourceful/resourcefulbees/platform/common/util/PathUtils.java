package com.teamresourceful.resourcefulbees.platform.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.file.Path;

public class PathUtils {

    @ExpectPlatform
    public static Path getConfigPath() {
        throw new NotImplementedException("Not implemented on this platform");
    }

    @ExpectPlatform
    public static Path getModPath(String modid) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
