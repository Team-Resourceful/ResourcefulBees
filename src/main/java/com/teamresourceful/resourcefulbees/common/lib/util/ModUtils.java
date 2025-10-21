package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefullib.common.utils.EnumBuilder;
import net.minecraft.world.entity.MobCategory;

public class ModUtils {

    public static MobCategory createMobCategory(String name, String id, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, MobCategory fallback) {
        //TODO REQUIRES EXTENSIVE TESTING AS THIS IS HACKY AF
        try {
            return EnumBuilder.of(MobCategory.class, name)
                    .withArg(String.class, id)
                    .withArg(int.class, max)
                    .withArg(boolean.class, isFriendly)
                    .withArg(boolean.class, isPersistent)
                    .withArg(int.class, despawnDistance)
                    .build();
        }catch (Throwable e) {
            e.printStackTrace();
            return fallback;
        }
    }
}
