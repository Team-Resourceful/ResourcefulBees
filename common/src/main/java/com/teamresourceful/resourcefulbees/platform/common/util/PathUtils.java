package com.teamresourceful.resourcefulbees.platform.common.util;

import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class PathUtils {

    @ExpectPlatform
    public static Path getConfigPath() {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static Path getModPath(String modid) {
        throw new NotImplementedError();
    }
}
