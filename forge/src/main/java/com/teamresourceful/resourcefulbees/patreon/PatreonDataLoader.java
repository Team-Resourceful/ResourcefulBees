package com.teamresourceful.resourcefulbees.patreon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class PatreonDataLoader {
    private static final Gson gson = new Gson();

    private PatreonDataLoader()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {
        PatreonInfo.clearPatreons();
        try {
            URL url = new URL("https://raw.githubusercontent.com/Resourceful-Bees/patreon-data/main/patreons.json");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String rep = IOUtils.toString(new BufferedInputStream(connection.getInputStream()), StandardCharsets.UTF_8);
            JsonObject json = gson.fromJson(rep, JsonObject.class);
            if (json.has("patreons")){
                JsonArray patreons = json.getAsJsonArray("patreons");
                patreons.forEach(jsonElement -> {
                    if (jsonElement.isJsonObject()){
                        JsonObject patreon = jsonElement.getAsJsonObject();
                        if (patreon.has("uuid")){
                            PatreonInfo.addPatreon(patreon.get("uuid").getAsString(), getRewardData(patreon));
                        }
                    }
                });
            }


        }catch (Exception ignored){
            //Does nothing
        }
    }


    private static BeeRewardData getRewardData(JsonObject patreon){
        BeeRewardData.BeeTextures textures = BeeRewardData.BeeTextures.BASE;
        Color color = patreon.has("color") ? Color.parse(patreon.get("color").getAsString()) : Color.DEFAULT;
        if (patreon.has("texture")){
            textures = BeeRewardData.BeeTextures.getTexture(patreon.get("texture").getAsString());
        }
        if (patreon.has("rainbow") && patreon.get("rainbow").getAsBoolean())
            color = Color.RAINBOW;
        return new BeeRewardData(color, textures);
    }
}
