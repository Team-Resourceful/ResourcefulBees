package com.teamresourceful.resourcefulbees.client.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.utils.WebUtils;

import java.util.Map;
import java.util.Optional;

public final class PetLoader {

    private static final String URL = "https://pets.resourcefulbees.com/pets";

    private PetLoader()  {
        throw new UtilityClassError();
    }

    public static void loadAPI() {

        JsonObject json = WebUtils.getJson(URL);
        if (json == null) return;
        try {
            if (json.get("models") instanceof JsonArray array) {
                array.forEach(PetLoader::addModel);
            }

            if (json.get("users") instanceof JsonObject object) {
                for (Map.Entry<String, JsonElement> user : object.entrySet()) {
                    if (user.getValue() instanceof JsonObject model) {
                        addModel(model).ifPresent(data -> PetInfo.addUser(user.getKey(), data.getId()));
                    } else if (user.getValue() instanceof JsonPrimitive) {
                        PetInfo.addUser(user.getKey(), user.getValue().getAsString());
                    }
                }
            }

            if (json.get("default") instanceof JsonPrimitive primitive) {
                PetInfo.defaultModel = PetInfo.getModel(primitive.getAsString());
            }
        }catch (Exception ignored){
            //Does nothing
        }
    }

    private static Optional<PetModelData> addModel(JsonElement element) {
        Optional<PetModelData> petData = PetModelData.CODEC.parse(JsonOps.INSTANCE, element).result();
        petData.ifPresent(PetInfo::addModel);
        return petData;
    }
}
