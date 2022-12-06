package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PathUtilsImpl {
    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getModPath(String modid) {
        return ModList.get().getModFileById(modid).getFile().getFilePath();
    }
}
