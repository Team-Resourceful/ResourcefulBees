package com.teamresourceful.resourcefulbees.platform.common.util;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

public class ModUtils {

    @ExpectPlatform
    public static void openEntityInJEI(EntityType<?> entity) {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static void spawnBabyEvent(SpawnBabyEvent event) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static BooleanObjectPair<Vec3> enderEntityTeleport(LivingEntity entity, double x, double y, double z) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    @SuppressWarnings("Contract")
    @Contract(value = "null -> false", pure = true)
    public static boolean isRealPlayer(Player player) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static void openScreen(Player player, MenuProvider provider, BlockPos pos) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static Level.ExplosionInteraction getExplosionInteraction(Level level, Entity entity) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static Fluid getFluid(LiquidBlock block) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static SpawnEggItem createCustomBeeSpawnEgg(Supplier<EntityType<? extends CustomBeeEntity>> entityType, String beeType) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static GenericMemoryPack createHiddenDataPack(String id, JsonObject meta) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    @Contract(pure = true)
    public static boolean isProduction() {
        throw new NotImplementedException();
    }
}
