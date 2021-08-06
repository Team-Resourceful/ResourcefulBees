package com.teamresourceful.resourcefulbees.client.pets;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetInfo {

    protected static BeeRewardData defaultBee = null;

    private static final Map<UUID, BeeRewardData> USER_PETS = new HashMap<>();

    public static Map<UUID, BeeRewardData> getUserPets(){
        return Collections.unmodifiableMap(USER_PETS);
    }

    private PetInfo()   {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    protected static void addUser(String uuid, BeeRewardData data){
        if (data != null) {
            try {
                USER_PETS.put(UUID.fromString(uuid), data);
            } catch (Exception ignored) {
                //does nothing
            }
        }
    }

    public static boolean hasPet(UUID uuid){
        return USER_PETS.containsKey(uuid) || defaultBee != null;
    }

    public static BeeRewardData getPet(UUID uuid){
        return USER_PETS.containsKey(uuid) ? USER_PETS.get(uuid) : defaultBee;
    }

    public static void clearPets(){
        USER_PETS.clear();
    }
}
