package com.teamresourceful.resourcefulbees.platform.common.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PathUtilsImpl {
    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getModPath(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .flatMap(container -> container.getRootPaths().stream().findFirst())
                .orElse(null);
    }
}
