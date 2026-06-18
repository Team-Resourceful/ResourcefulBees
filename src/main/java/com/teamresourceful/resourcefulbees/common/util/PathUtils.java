package com.teamresourceful.resourcefulbees.common.util;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PathUtils {

    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getModPath(String modId) {
        return ModList.get().getModFileById(modId).getFile().getFilePath();
    }
}
