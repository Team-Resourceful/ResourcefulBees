package com.teamresourceful.resourcefulbees.client.pets;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class PetLoader {

    private PetLoader()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {
        try {
            URL url = new URL("https://gist.githubusercontent.com/ThatGravyBoat/28b0d4dc1e1fa2ec341d7ef245519e4c/raw/d97751204f17370ac8cc7352d668d1f3b6cb8d93/users.json");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String rep = IOUtils.toString(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
            JsonObject json = ModConstants.GSON.fromJson(rep, JsonObject.class);

            //TODO 1.17 change all to an inline instanceof.

            JsonElement models = json.get("models");
            if (models instanceof JsonArray) {
                JsonArray modelData = models.getAsJsonArray();
                for (JsonElement model : modelData) {
                    Optional<PetModelData> petData = PetModelData.CODEC.parse(JsonOps.INSTANCE, model).result();
                    petData.ifPresent(PetInfo::addModel);
                }
            }

            JsonElement users = json.get("users");
            if (users instanceof JsonObject) {
                JsonObject userObject = users.getAsJsonObject();
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
