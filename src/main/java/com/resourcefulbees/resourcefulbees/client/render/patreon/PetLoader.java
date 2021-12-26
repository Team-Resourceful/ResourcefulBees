package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class PetLoader {

    private static final Gson gson = new Gson();

    private PetLoader()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {
        try {
            URL url = new URL("https://pets.resourcefulbees.com/pets");
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.76 (Resourceful Bees Mod)");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String rep = IOUtils.toString(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
            JsonObject json = gson.fromJson(rep, JsonObject.class);

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
