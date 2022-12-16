package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
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

    public static void spawnBabyEvent(SpawnBabyEvent event) {
        SpawnBabyEvent.EVENT.fire(event);
        final BabyEntitySpawnEvent forgeEvent = new BabyEntitySpawnEvent(event.parent1(), event.parent2(), event.getChild());
        MinecraftForge.EVENT_BUS.post(forgeEvent);
        if (forgeEvent.isCanceled()) {
            event.setCanceled(true);
        }
        event.setChild(forgeEvent.getChild());
    }
}
