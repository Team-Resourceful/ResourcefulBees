package com.resourcefulbees.resourcefulbees.patreon;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PatreonInfo {

    private static BeeRewardData defaultBee = null;
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

    protected static void setDefaultBee(BeeRewardData data) {
        defaultBee = data;
    }

    public static boolean isPatreon(UUID uuid){
        return patreons.containsKey(uuid) || defaultBee != null;
    }

    public static BeeRewardData getPatreon(UUID uuid){
        BeeRewardData data = patreons.get(uuid);
        return data != null ? data : defaultBee;
    }

    public static void clearPatreons(){
        patreons.clear();
        defaultBee = null;
    }
}
