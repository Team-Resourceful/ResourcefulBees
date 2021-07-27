package com.teamresourceful.resourcefulbees.patreon;

import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PatreonInfo {

    private static final Map<UUID, BeeRewardData> patreons = new HashMap<>();

    public static Map<UUID, BeeRewardData> getPatreons(){
        return Collections.unmodifiableMap(patreons);
    }

    private PatreonInfo()   {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    protected static void addPatreon(String uuid, BeeRewardData data){
        if (data != null) {
            try {
                patreons.put(UUID.fromString(uuid), data);
            } catch (Exception ignored) {
                //does nothing
            }
        }
    }

    public static boolean isPatreon(UUID uuid){
        return patreons.containsKey(uuid);
    }

    public static BeeRewardData getPatreon(UUID uuid){
        return patreons.get(uuid);
    }

    public static void clearPatreons(){
        patreons.clear();
    }
}
