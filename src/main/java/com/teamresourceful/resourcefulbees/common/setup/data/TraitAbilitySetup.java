package com.teamresourceful.resourcefulbees.common.setup.data;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbilityType;
import com.teamresourceful.resourcefulbees.common.registries.custom.TraitAbilityTypeRegistry;
import com.teamresourceful.resourcefullib.common.exceptions.ValidationException;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TraitAbilitySetup {

    private static final String ABILITY_PATH = "resourcefulbees/abilities/";

    public static Map<Identifier, TraitAbility> loadAbilities(ResourceManager resourceManager) {
        Map<Identifier, TraitAbility> abilities = new LinkedHashMap<>();

        resourceManager.listResources(
                "resourcefulbees/abilities",
                id -> id.getPath().endsWith(".json")
        ).forEach((resourceId, resource) -> {

            String path = resourceId.getPath();

            String name = path.substring(ABILITY_PATH.length(), path.length() - ".json".length());

            Identifier id = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), name);

            try (Reader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.fromJson(Constants.GSON, reader, JsonObject.class);

                Identifier typeId = Identifier.parse(GsonHelper.getAsString(json, "type"));

                TraitAbilityType<?> type = TraitAbilityTypeRegistry.get().getType(typeId);

                if (type == null) {
                    throw new IllegalStateException(
                            "Unknown trait ability type: " + typeId
                    );
                }

                TraitAbility ability = type.codec()
                        .codec()
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow(error -> new ValidationException(
                                "Failed to parse ability " + id
                                        + ": " + error
                        ));

                abilities.put(id, ability);

            } catch (IOException exception) {
                throw new UncheckedIOException(
                        "Failed to load trait ability " + id,
                        exception
                );
            }
        });

        return abilities;
    }
}