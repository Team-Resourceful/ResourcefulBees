package com.teamresourceful.resourcefulbees.platform.common.util.fabric;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.util.EnumBuilder;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ModUtilsImpl {

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

    public static void spawnBabyEvent(SpawnBabyEvent event) {
        SpawnBabyEvent.EVENT.fire(event);
        //TODO check for fabric events
    }

    public static boolean isRealPlayer(Player player) {
        return player != null && player.getClass() == ServerPlayer.class;
    }

    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        return BuiltInRegistries.ACTIVITY.key(); //TODO check for fabric registry
    }

    public static void openScreen(Player player, MenuProvider provider, BlockPos pos) {
    }

    public static Explosion.BlockInteraction getExplosionInteraction(Level level, Entity entity) {
        return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
    }

    public static BooleanObjectPair<Vec3> enderEntityTeleport(LivingEntity entity, double x, double y, double z) {
        return BooleanObjectPair.of(false, new Vec3(x, y, z));
    }

    public static Fluid getFluid(LiquidBlock block) {
        return Fluids.EMPTY; //TODO check for fabric fluid
    }

    public static SpawnEggItem createCustomBeeSpawnEgg(Supplier<EntityType<? extends CustomBeeEntity>> entityType, String beeType) {
        return new SpawnEggItem(entityType.get(), 0xffffff, 0x000000, new Item.Properties());
    }

    public static GenericMemoryPack createHiddenDataPack(String id, JsonObject meta) {
        return new GenericMemoryPack(PackType.SERVER_DATA, id, meta) {};
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
