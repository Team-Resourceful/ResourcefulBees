package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import com.teamresourceful.resourcefullib.common.utils.forge.HiddenGenericMemoryPack;
import it.unimi.dsi.fastutil.booleans.BooleanObjectImmutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModUtilsImpl {
    public static void openEntityInJEI(EntityType<?> entity) {
        JEICompat.searchEntity(entity);
    }

    public static MobCategory createMobCategory(String name, String id, int max, boolean isFriendly, boolean isPersistent, int despawnDistance, MobCategory fallback) {
        try {
            return MobCategory.create(name, id, max, isFriendly, isPersistent, despawnDistance);
        } catch (Throwable e) {
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

    public static BooleanObjectPair<Vec3> enderEntityTeleport(LivingEntity entity, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, x, y, z);
        return new BooleanObjectImmutablePair<>(
                MinecraftForge.EVENT_BUS.post(event),
                new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ())
        );
    }

    public static boolean isRealPlayer(Player player) {
        return player != null && !(player instanceof FakePlayer);
    }

    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        return ForgeRegistries.Keys.BIOME_MODIFIERS;
    }

    public static void openScreen(Player player, MenuProvider provider, BlockPos pos) {
        NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, provider, pos);
    }

    public static Level.ExplosionInteraction getExplosionInteraction(Level level, Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(level, entity) ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
    }

    public static Fluid getFluid(LiquidBlock block) {
        return block.getFluid();
    }

    public static SpawnEggItem createCustomBeeSpawnEgg(Supplier<EntityType<? extends CustomBeeEntity>> entityType, String beeType) {
        return new BeeSpawnEggItem(entityType, beeType);
    }

    public static GenericMemoryPack createHiddenDataPack(String id, JsonObject meta) {
        return new HiddenGenericMemoryPack(PackType.SERVER_DATA, id, meta) {
            // I don't know why I made HiddenGenericMemoryPack protected.
        };
    }
}
