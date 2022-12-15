package com.teamresourceful.resourcefulbees.platform.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.apache.commons.lang3.NotImplementedException;

public class ModUtils {

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new NotImplementedException("Not implemented on this platform");
    }

    @ExpectPlatform
    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedException("Not implemented on this platform");
    }

    /**
     * Creates a new MobCategory
     * @param name The internal id of the enum ie. MONSTER or CREATURE (This is what it would be named if you named it in the enum itself)
     * @param id The name of the enum ie. monster or creature
     * @param max The max number of entities of this type that can spawn in a chunk
     * @param isFriendly If the entity is friendly to players
     * @param isPersistent If the entity is persistent
     * @param despawnDistance The distance at which the entity will despawn
     */
    @ExpectPlatform
    public static MobCategory createMobCategory(String name, String id, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, MobCategory fallback) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
