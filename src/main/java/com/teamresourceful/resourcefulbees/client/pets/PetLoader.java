package com.teamresourceful.resourcefulbees.client.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class PetLoader {

    private static final String URL = "https://gist.githubusercontent.com/ThatGravyBoat/28b0d4dc1e1fa2ec341d7ef245519e4c/raw/d97751204f17370ac8cc7352d668d1f3b6cb8d93/users.json";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    private PetLoader()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadAPI() {
        try {
            IModFileInfo modFile = ModList.get().getModFileById(ResourcefulBees.MOD_ID);
            String version = modFile != null ? modFile.versionString() : "Unknown Version";

            HttpRequest request = HttpRequest.newBuilder(new URI(URL))
                    .GET()
                    .version(HttpClient.Version.HTTP_2)
                    .header("User-Agent", "Minecraft Mod (" + ResourcefulBees.MOD_ID + "/" + version + ")")
                    .build();

            HttpResponse<String> send = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = ModConstants.GSON.fromJson(send.body(), JsonObject.class);

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
