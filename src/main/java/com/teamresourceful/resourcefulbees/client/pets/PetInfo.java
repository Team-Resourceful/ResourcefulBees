package com.teamresourceful.resourcefulbees.client.pets;

import com.mojang.util.UUIDTypeAdapter;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("ProtectedMemberInFinalClass")
public final class PetInfo {

    private static final int VERSION = 1;

    private static final PetModelData FALLBACK_MODEL = new PetModelData(-1, "fallback", new ResourceLocation("resourcefulbees:geo/base-nocloak.geo.json"), new ResourceLocation("textures/entity/bee/bee.png"), new HashSet<>());

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
        if (data.getVersion() != -1 && data.getVersion() <= VERSION) PET_MODELS.put(data.getId(), data);
        else PET_MODELS.put(data.getId(), FALLBACK_MODEL);
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
