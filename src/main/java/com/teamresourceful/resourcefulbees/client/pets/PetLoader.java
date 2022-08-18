package com.teamresourceful.resourcefulbees.client.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.utils.WebUtils;

import java.util.Map;
import java.util.Optional;

public final class PetLoader {

    private static final String URL = "https://gist.githubusercontent.com/ThatGravyBoat/28b0d4dc1e1fa2ec341d7ef245519e4c/raw/d97751204f17370ac8cc7352d668d1f3b6cb8d93/users.json";

    private PetLoader()  {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
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
