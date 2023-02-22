package com.teamresourceful.resourcefulbees.platform.common.util;

import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;

public class ModUtils {

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedError();
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
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static void spawnBabyEvent(SpawnBabyEvent event) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    @SuppressWarnings("Contract")
    @Contract(value = "null -> false", pure = true)
    public static boolean isRealPlayer(Player player) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static void openScreen(Player player, MenuProvider provider, BlockPos pos) {
        throw new NotImplementedError();
    }

    @ExpectPlatform
    public static Explosion.BlockInteraction getExplosionInteraction(Level level, Entity entity) {
        throw new NotImplementedError();
    }
}
