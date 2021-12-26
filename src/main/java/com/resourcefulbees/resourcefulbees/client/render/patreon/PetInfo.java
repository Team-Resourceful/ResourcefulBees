package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.util.UUIDTypeAdapter;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PetInfo {

    protected static PetModelData defaultModel = null;

    private static final Map<String, PetModelData> PET_MODELS = new HashMap<>();
    private static final Map<UUID, String> USER_PETS = new HashMap<>();

    private PetInfo()   {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    protected static void addUser(String uuid, String model){
        try {
            getUUID(uuid).ifPresent(uid -> USER_PETS.put(uid, model));
        } catch (Exception ignored) {
            //does nothing
        }
    }

    private static Optional<UUID> getUUID(String uuid) throws IllegalArgumentException {
        if (uuid.length() == 36 && uuid.contains("-")) {
            return Optional.of(UUIDTypeAdapter.fromString(uuid));
        } else if (uuid.length() == 32) {
            return Optional.of(UUID.fromString(uuid));
        }
        return Optional.empty();
    }

    protected static void addModel(@NotNull PetModelData data) {
        PET_MODELS.put(data.getId(), data);
    }

    public static boolean hasPet(UUID uuid){
        return USER_PETS.containsKey(uuid) || defaultModel != null;
    }

    @Nullable
    public static PetModelData getModel(String id) {
        return PET_MODELS.get(id);
    }

    @Nullable
    public static PetModelData getPet(UUID uuid){
        PetModelData data = getModel(USER_PETS.get(uuid));
        if (data != null) return data;
        if (defaultModel != null) return defaultModel;
        USER_PETS.remove(uuid); //To ensure we do not waste time dealing with null values.
        return null;
    }
}
