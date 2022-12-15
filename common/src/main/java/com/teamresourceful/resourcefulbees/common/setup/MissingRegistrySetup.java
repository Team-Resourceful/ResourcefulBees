package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneycombRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.util.PathUtils;
import com.teamresourceful.resourcefullib.common.utils.readers.ArrayByteReader;
import com.teamresourceful.resourcefullib.common.yabn.YabnParser;
import com.teamresourceful.resourcefullib.common.yabn.base.YabnArray;
import com.teamresourceful.resourcefullib.common.yabn.base.YabnElement;
import com.teamresourceful.resourcefullib.common.yabn.base.YabnObject;
import com.teamresourceful.resourcefullib.common.yabn.base.YabnPrimitive;
import com.teamresourceful.resourcefullib.common.yabn.base.primitives.StringContents;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public final class MissingRegistrySetup {

    private static boolean anyRegistriesMissing = false;

    public static void checkMissingRegistries() {
        File registryCheckFile = Paths.get(PathUtils.getConfigPath().toAbsolutePath().toString(), ModConstants.MOD_ID, "registry.yabn").toFile();
        try {
            YabnElement parse = YabnParser.parse(new ArrayByteReader(FileUtils.readFileToByteArray(registryCheckFile)));
            if (parse instanceof YabnObject object) {
                if (object.get("b") instanceof YabnArray array) {
                    StringBuilder missingBees = new StringBuilder();
                    getStrings(array).stream()
                            .filter(s -> !BeeRegistry.get().containsBeeType(s) && (anyRegistriesMissing = true))
                            .forEach(s -> missingBees.append("    - ").append(s).append("\n"));
                    if (!missingBees.isEmpty()) ModConstants.LOGGER.warn("\nThe following bees are missing from the registry: \n {}", missingBees);
                }
                if (object.get("h") instanceof YabnArray array) {
                    StringBuilder missingHoney = new StringBuilder();
                    getStrings(array).stream()
                            .filter(s -> !HoneyRegistry.get().containsHoney(s) && (anyRegistriesMissing = true))
                            .forEach(s -> missingHoney.append("    - ").append(s).append("\n"));
                    if (!missingHoney.isEmpty()) ModConstants.LOGGER.warn("\nThe following honey are missing from the registry: \n {}", missingHoney);
                }
                if (object.get("c") instanceof YabnArray array) {
                    StringBuilder missingCombs = new StringBuilder();
                    getStrings(array).stream()
                            .filter(s -> !HoneycombRegistry.get().containsHoneycomb(s) && (anyRegistriesMissing = true))
                            .forEach(s -> missingCombs.append("    - ").append(s).append("\n"));
                    if (!missingCombs.isEmpty()) ModConstants.LOGGER.warn("\nThe following combs are missing from the registry: \n {}", missingCombs);
                }
            }
        } catch (Exception e) {
            ModConstants.LOGGER.error("Failed to read registry check file. Registries may be missing.");
            if (ModConstants.LOGGER.isDebugEnabled())
                e.printStackTrace();
        }

        YabnObject object = new YabnObject();
        YabnArray beeArray = new YabnArray();
        YabnArray honeyArray = new YabnArray();
        YabnArray combArray = new YabnArray();
        BeeRegistry.get().getBeeTypes().forEach(bee -> beeArray.add(YabnPrimitive.ofString(bee)));
        HoneyRegistry.get().getHoneyTypes().forEach(honey -> honeyArray.add(YabnPrimitive.ofString(honey)));
        HoneycombRegistry.get().getHoneycombTypes().forEach(comb -> combArray.add(YabnPrimitive.ofString(comb)));
        object.put("b", beeArray);
        object.put("h", honeyArray);
        object.put("c", combArray);
        try {
            FileUtils.writeByteArrayToFile(registryCheckFile, object.toData());
        } catch (Exception e) {
            ModConstants.LOGGER.error("Failed to write registry check file. Registries may be missing next run!");
        }
    }

    private static List<String> getStrings(YabnArray array) {
        return array.elements().stream()
            .filter(YabnPrimitive.class::isInstance)
            .map(YabnPrimitive.class::cast)
            .map(YabnPrimitive::contents)
            .filter(StringContents.class::isInstance)
            .map(StringContents.class::cast)
            .map(StringContents::value)
            .toList();
    }

    public static boolean isMissingRegistries() {
        return anyRegistriesMissing;
    }
}
