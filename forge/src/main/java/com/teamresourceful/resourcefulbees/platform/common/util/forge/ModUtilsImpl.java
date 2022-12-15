package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.ModList;

public class ModUtilsImpl {
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static void openEntityInJEI(EntityType<?> entity) {
        JEICompat.searchEntity(entity);
    }

    public static MobCategory createMobCategory(String name, String id, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, MobCategory fallback) {
        try {
            return MobCategory.create(name, id, max, isFriendly, isPersistent, despawnDistance);
        }catch (Throwable e) {
            e.printStackTrace();
            return fallback;
        }
    }
}
