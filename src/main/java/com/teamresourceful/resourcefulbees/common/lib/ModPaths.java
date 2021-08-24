package com.teamresourceful.resourcefulbees.common.lib;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class ModPaths {
    private ModPaths() {
        throw new IllegalArgumentException(ModConstants.UTILITY_CLASS);
    }

    public static final Path BEES = createCustomPath("bees");
    public static final Path BIOME_DICTIONARY = createCustomPath("biome_dictionary");
    public static final Path BEE_TRAITS = createCustomPath("bee_traits");
    public static final Path RESOURCES = createCustomPath("resources");
    public static final Path HONEY = createCustomPath("honey");
    public static final Path HONEYCOMBS = createCustomPath("honeycombs");


    private static Path createCustomPath(String pathName) {
        Path customPath = Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), ResourcefulBees.MOD_ID, pathName);
        createDirectory(customPath, pathName);
        return customPath;
    }

    private static void createDirectory(Path path, String dirName) {
        try { Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) { LOGGER.error("failed to create \"{}\" directory", dirName);}
    }
}
