package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.exceptions.ValidationException;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import com.teamresourceful.resourcefullib.common.utils.FileUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class TraitSetup {

    private TraitSetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Prepares default trait files during initialization.
     * This should not run on every reload.
     */
    public static void prepareTraitResources() {
        if (GeneralConfig.generateDefaults) {
            ModConstants.LOGGER.info("Copying Default Traits...");

            FileUtils.copyDefaultFiles(
                    "/data/resourcefulbees/defaults/default_traits",
                    ModPaths.BEE_TRAITS,
                    ModPaths.MOD_ROOT,
                    FileUtils::isJson
            );
        }
    }

    /**
     * Loads all trait definitions without modifying the active registry.
     */
    public static Map<Identifier, Trait> loadTraits(ResourceManager resourceManager) {
        Map<Identifier, Trait> traits = new LinkedHashMap<>();

        if (GeneralConfig.enableDevBees) {
            ModConstants.LOGGER.info("Loading Dev Traits...");
            FileUtils.setupDevResources(
                    "/data/resourcefulbees/dev/dev_traits",
                    (reader, name) -> addTrait(traits, toIdentifier(name), reader),
                    ModPaths.MOD_ROOT,
                    FileUtils::isJson
            );
        }

        if (GeneralConfig.enableSupporterBees) {
            ModConstants.LOGGER.info("Loading Supporter Traits...");
            FileUtils.setupDevResources(
                    "/data/resourcefulbees/supporter/traits",
                    (reader, name) -> addTrait(traits, toIdentifier(name), reader),
                    ModPaths.MOD_ROOT,
                    FileUtils::isJson
            );
        }

        ModConstants.LOGGER.info("Loading Custom Traits...");
        FileUtils.streamFilesAndParse(
                ModPaths.BEE_TRAITS,
                (reader, name) -> addTrait(traits, toIdentifier(name), reader),
                FileUtils::isJson
        );

//        ModConstants.LOGGER.info("Loading External Mod Traits...");
//        ModDataDiscovery.discover("traits", (id, reader) -> addTrait(traits, id, reader));

        ModConstants.LOGGER.info("Loading Datapack Traits...");
        loadDatapackTraits(resourceManager, traits);

        return traits;
    }

    private static final String TRAIT_PATH = "resourcefulbees/traits/";

    private static void loadDatapackTraits(ResourceManager resourceManager, Map<Identifier, Trait> traits) {
        var resources = resourceManager.listResources(
                "resourcefulbees/traits",
                id -> id.getPath().endsWith(".json")
        );

        ModConstants.LOGGER.info(
                "Available datapack namespaces: {}",
                resourceManager.getNamespaces()
        );

        ModConstants.LOGGER.info(
                "Discovered trait resources: {}",
                resources.keySet()
        );

        resources.forEach((resourceId, resource) -> {
            String path = resourceId.getPath();

            String traitPath = path.substring(TRAIT_PATH.length(), path.length() - ".json".length());

            Identifier traitId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), traitPath);

            try (Reader reader = resource.openAsReader()) {
                Trait trait = parseTrait(traitId, reader);

                // Datapack definitions override earlier trait sources.
                traits.put(traitId, trait);

                ModConstants.LOGGER.debug(
                        "Loaded datapack trait {} from {}",
                        traitId,
                        resourceId
                );
            } catch (IOException exception) {
                throw new UncheckedIOException(
                        "Failed to read datapack trait " + resourceId,
                        exception
                );
            }
        });
    }

    private static Identifier toIdentifier(String name) {
        return ModIdentifier.of(name.toLowerCase(Locale.ROOT).replace(" ", "_"));
    }

    private static void addTrait(Map<Identifier, Trait> traits, Identifier id, Reader reader) {
        Trait trait = parseTrait(id, reader);

        if (traits.putIfAbsent(id, trait) != null) {
            throw new IllegalStateException(
                    "Duplicate trait definition: " + id
            );
        }
    }

    public static Trait parseTrait(Identifier id, Reader reader) {
        JsonObject json = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);

        return Trait.getCodec(id)
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow(error -> new ValidationException(
                        "Could not create trait " + id + ": " + error
                ));
    }
}