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
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {

        JsonObject json = WebUtils.getJson(URL);
        if (json == null) return;
        try {
            JsonElement models = json.get("models");
            if (models instanceof JsonArray modelData) {
                for (JsonElement model : modelData) {
                    Optional<PetModelData> petData = PetModelData.CODEC.parse(JsonOps.INSTANCE, model).result();
                    petData.ifPresent(PetInfo::addModel);
                }
            }

            JsonElement users = json.get("users");
            if (users instanceof JsonObject userObject) {
                for (Map.Entry<String, JsonElement> user : userObject.entrySet()) {
                    if (user.getValue() instanceof JsonPrimitive)
                        PetInfo.addUser(user.getKey(), user.getValue().getAsString());
                }
            }

            JsonElement defaultBee = json.get("default");
            if (defaultBee instanceof JsonPrimitive) {
                PetInfo.defaultModel = PetInfo.getModel(defaultBee.getAsString());
            }
        }catch (Exception ignored){
            //Does nothing
        }
    }
}
