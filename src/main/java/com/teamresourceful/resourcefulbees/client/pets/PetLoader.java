package com.teamresourceful.resourcefulbees.client.pets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class PetLoader {
    private static final Gson gson = new Gson();

    private PetLoader()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {
        PetInfo.clearPets();
        if (true) return;
        //TODO REQUIRED disable this and change the URL to the server url. I wont do it right now as the server is still a WIP
        // and should not be given out to the public yet.

        try {
            URL url = new URL("https://raw.githubusercontent.com/Resourceful-Bees/patreon-data/main/patreons.json");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String rep = IOUtils.toString(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
            JsonObject json = gson.fromJson(rep, JsonObject.class);
            if (json.has("default") && json.get("default").isJsonObject()) {
                PetInfo.defaultBee = getRewardData(json.get("default").getAsJsonObject());
            }

            if (json.has("users")){
                for (JsonElement user : json.getAsJsonArray("users")) {
                    if (!user.isJsonObject()) continue;

                    JsonObject userObject = user.getAsJsonObject();
                    if (userObject.has("uuid")){
                        PetInfo.addUser(userObject.get("uuid").getAsString(), getRewardData(userObject));
                    }
                }
            }


        }catch (Exception ignored){
            //Does nothing
        }
    }


    private static BeeRewardData getRewardData(JsonObject patreon){
        BeeRewardData.BeeTextures textures = BeeRewardData.BeeTextures.BASE;
        Color color = Color.DEFAULT;
        if (patreon.has("color") && patreon.get("color").isJsonPrimitive()) {
            //replace with inline variable for the isJsonPrimitive and replace with a normal instanceof in 1.17
            JsonPrimitive colorValue = patreon.get("color").getAsJsonPrimitive();
            if (colorValue.isNumber()) color = new Color(colorValue.getAsInt());
            else if (colorValue.isString()) {
                if (colorValue.getAsString().startsWith("#") || colorValue.getAsString().startsWith("0x")) {
                    color = Color.parse(patreon.get("color").getAsString());
                } else if (colorValue.getAsString().equalsIgnoreCase("rainbow")) {
                    color = Color.RAINBOW;
                }
            }
        }
        if (patreon.has("texture") && patreon.get("texture").isJsonPrimitive() && patreon.get("texture").getAsJsonPrimitive().isString()){
            textures = BeeRewardData.BeeTextures.getTexture(patreon.get("texture").getAsString());
        }
        return new BeeRewardData(color, textures);
    }
}
