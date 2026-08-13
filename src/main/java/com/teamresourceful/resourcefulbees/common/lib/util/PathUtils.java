package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PathUtils {

    private PathUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getModPath(String modId) {
        //ResourcefulBees.LOGGER.debug("mod path:");
        //ResourcefulBees.LOGGER.debug(ModList.get().getModFileById(modId).getFile().getFilePath().toString());
        //ResourcefulBees.LOGGER.debug(ModList.get().getModFileById(modId).getFile().getFilePath().getParent().getParent().getParent().resolve("resources/main").toString());
        if (ModUtils.isProduction()) return ModList.get().getModFileById(modId).getFile().getFilePath();
        return ModList.get().getModFileById(modId).getFile().getFilePath().getParent().getParent().getParent().resolve("resources/main");
        //this is because getModFileById.getFile.getFilePath returns build/classes/main and not build/resources/main for whatever reason
        //so we have to differentiate dev and production since it works fine in production. it's a better alternative to using
        //class.getClassLoader.getResource which still has the caveat of needing to handle both dev and production separately
        //and we already have file handling logic for dev/prod
    }
}
