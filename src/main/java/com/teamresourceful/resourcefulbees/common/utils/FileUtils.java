package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.fml.ModList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class FileUtils {

    public static final String JSON = ".json";
    public static final String ZIP = ".zip";
    public static final Path MOD_ROOT = ModList.get().getModFileById(ResourcefulBees.MOD_ID).getFile().getFilePath();

    private FileUtils() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static void streamFilesAndParse(Path directoryPath, BiConsumer<Reader, String> instructions, String errorMessage) {
        try (Stream<Path> zipStream = Files.walk(directoryPath);
             Stream<Path> jsonStream = Files.walk(directoryPath)) {
            zipStream.filter(f -> f.getFileName().toString().endsWith(ZIP)).forEach(path -> addZippedFile(path, instructions));
            jsonStream.filter(f -> f.getFileName().toString().endsWith(JSON)).forEach(path -> addFile(path, instructions));
        } catch (IOException e) {
            LOGGER.error(errorMessage, e);
        }
    }

    private static void addFile(Path path, BiConsumer<Reader, String> instructions) {
        File f = path.toFile();
        try {
            parseType(f, instructions);
        } catch (IOException e) {
            LOGGER.warn("File not found: {}", path);
        }
    }

    private static void addZippedFile(Path file, BiConsumer<Reader, String> instructions) {
        try (ZipFile zf = new ZipFile(file.toString())) {
            zf.stream()
                    .filter(zipEntry -> zipEntry.getName().endsWith(JSON))
                    .forEach(zipEntry -> {
                        try {
                            parseType(zf, zipEntry, instructions);
                        } catch (IOException e) {
                            LOGGER.error("Could not parse zip entry: {}", zipEntry.getName());
                        }
                    });
        } catch (IOException e) {
            LOGGER.warn("Could not read Zip File: {}", file.getFileName());
        }
    }

    private static void parseType(File file, BiConsumer<Reader, String> consumer) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

        Reader r = Files.newBufferedReader(file.toPath());

        consumer.accept(r, name);
    }

    private static void parseType(ZipFile zf, ZipEntry zipEntry, BiConsumer<Reader, String> consumer) throws IOException {
        String name = zipEntry.getName();
        name = name.substring(name.lastIndexOf("/") + 1, name.indexOf('.'));

        InputStream input = zf.getInputStream(zipEntry);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

        consumer.accept(reader, name);
    }

    public static void setupDefaultFiles(String dataPath, Path targetPath) {
        if (Files.isRegularFile(MOD_ROOT)) {
            try(FileSystem fileSystem = FileSystems.newFileSystem(MOD_ROOT)) {
                Path path = fileSystem.getPath(dataPath);
                if (Files.exists(path)) {
                    copyFiles(path, targetPath);
                }
            } catch (IOException e) {
                LOGGER.error("Could not load source {}!!", MOD_ROOT);
                e.printStackTrace();
            }
        } else if (Files.isDirectory(MOD_ROOT)) {
            copyFiles(Paths.get(MOD_ROOT.toString(), dataPath), targetPath);
        }
    }

    private static void copyFiles(Path source, Path targetPath) {
        try (Stream<Path> sourceStream = Files.walk(source)) {
            sourceStream.filter(f -> f.getFileName().toString().endsWith(JSON))
                    .forEach(path -> {
                        try {
                            Files.copy(path, Paths.get(targetPath.toString(), path.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            LOGGER.error("Could not copy file: {}, Target: {}", path, targetPath);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Could not stream source files: {}", source);
        }
    }

    public static void setupDevResources(String devPath, BiConsumer<Reader, String> parser, String errorMessage) {
        if (Files.isRegularFile(MOD_ROOT)) {
            try(FileSystem fileSystem = FileSystems.newFileSystem(MOD_ROOT)) {
                Path path = fileSystem.getPath(devPath);
                if (Files.exists(path)) {
                    FileUtils.streamFilesAndParse(path, parser, errorMessage);
                }
            } catch (IOException e) {
                LOGGER.error("Could not load source {}!", MOD_ROOT);
                e.printStackTrace();
            }
        } else if (Files.isDirectory(MOD_ROOT)) {
            FileUtils.streamFilesAndParse(Paths.get(MOD_ROOT.toString(), devPath), parser, errorMessage);
        }
    }
}
