package com.teamresourceful.resourcefulbees.platform.common.util.fabric;

import com.teamresourceful.resourcefulbees.common.util.EnumBuilder;
import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModUtilsImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedError();
    }

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
