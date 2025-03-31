package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefulbees.platform.common.util.PathUtils;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class  ModPaths {
    private ModPaths() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final Path MOD_ROOT = PathUtils.getModPath(ModConstants.MOD_ID);
    public static final Path BEES = createCustomPath("bees");
    public static final Path BEE_TRAITS = createCustomPath("bee_traits");
    public static final Path RESOURCES = createCustomPath("resources");
    public static final Path HONEY = createCustomPath("honey");
    public static final Path HONEYCOMBS = createCustomPath("honeycombs");


    private static Path createCustomPath(String pathName) {
        Path customPath = Paths.get(PathUtils.getConfigPath().toAbsolutePath().toString(), ModConstants.MOD_ID, pathName);
        createDirectory(customPath, pathName);
        return customPath;
    }

    private static void createDirectory(Path path, String dirName) {
        try { Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) { ModConstants.LOGGER.error("failed to create \"{}\" directory", dirName);}
    }
}
