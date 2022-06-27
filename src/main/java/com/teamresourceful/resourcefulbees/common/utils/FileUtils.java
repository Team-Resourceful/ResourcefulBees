package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class FileUtils {

    private static final Path MOD_ROOT = ModList.get().getModFileById(ResourcefulBees.MOD_ID).getFile().getFilePath();

    private FileUtils() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    private static String failedToLoadSourceError(Path path) {
        return String.format("Failed to load source: %s!", path);
    }

    private static boolean isZip(@NotNull Path f) {
        return f.toString().endsWith(".zip");
    }

    private static boolean isJson(@NotNull Path f) {
        return f.toString().endsWith(".json");
    }

    public static void streamFilesAndParse(Path source, BiConsumer<Reader, String> parser) {
        streamFiles(source, FileUtils::isJson, path -> readFileAndParse(path, parser));
        streamFiles(source, FileUtils::isZip, path -> openZipAndParse(path, parser));
    }

    private static void streamFiles(Path source, Predicate<Path> filter, Consumer<Path> handler) {
        try (Stream<Path> pathStream = Files.walk(source)) {
            pathStream.filter(filter).forEach(handler);
        } catch (IOException e) {
            LOGGER.error(failedToLoadSourceError(source), e);
        }
    }

    private static void openZipAndParse(Path source, BiConsumer<Reader, String> parser) {
        try(FileSystem zip = FileSystems.newFileSystem(source)) {
            zip.getRootDirectories().forEach(rootPath -> streamFiles(rootPath, FileUtils::isJson, path -> readFileAndParse(path, parser)));
        } catch (IOException e) {
            LOGGER.error(failedToLoadSourceError(source), e);
        }
    }

    private static void readFileAndParse(@NotNull Path filePath, @NotNull BiConsumer<Reader, String> parser) {
        try (Reader r = Files.newBufferedReader(filePath)) {
            String name = filePath.getFileName().toString();
            name = name.substring(0, name.indexOf('.'));
            parser.accept(r, name);
        } catch (IOException e) {
            LOGGER.error("Could not read file: {}", filePath, e);
        }
    }

    public static void copyDefaultFiles(String dataPath, Path targetPath) {
        if (Files.isRegularFile(MOD_ROOT)) {
            try(FileSystem fileSystem = FileSystems.newFileSystem(MOD_ROOT)) {
                streamFiles(fileSystem.getPath(dataPath), FileUtils::isJson, path -> copyFile(targetPath, path));
            } catch (IOException e) {
                LOGGER.error(failedToLoadSourceError(MOD_ROOT), e);
            }
        } else if (Files.isDirectory(MOD_ROOT)) {
            streamFiles(Paths.get(MOD_ROOT.toString(), dataPath), FileUtils::isJson, path1 -> copyFile(targetPath, path1));
        }
    }

    private static void copyFile(@NotNull Path targetPath, Path source) {
        try {
            Files.copy(source, Paths.get(targetPath.toString(), source.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Could not copy file: {}, Target: {}", source, targetPath);
        }
    }

    public static void setupDevResources(@NotNull String devPath, @NotNull BiConsumer<Reader, String> parser) {
        if (Files.isRegularFile(MOD_ROOT)) { //production
            try(FileSystem fileSystem = FileSystems.newFileSystem(MOD_ROOT)) {
                streamFilesAndParse(fileSystem.getPath(devPath), parser);
            } catch (IOException e) {
                LOGGER.error(failedToLoadSourceError(MOD_ROOT), e);
            }
        } else if (Files.isDirectory(MOD_ROOT)) { //userDev
            streamFilesAndParse(Paths.get(MOD_ROOT.toString(), devPath), parser);
        }
    }
}
