package com.teamresourceful.resourcefulbees.platform.common.util.forge;

import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefulbees.platform.common.events.SpawnBabyEvent;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static boolean isRealPlayer(Player player) {
        return player != null && !(player instanceof FakePlayer);
    }

    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        return ForgeRegistries.Keys.BIOME_MODIFIERS;
    }

    public static void openScreen(Player player, MenuProvider provider, BlockPos pos) {
        NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, provider, pos);
    }

    public static Explosion.BlockInteraction getExplosionInteraction(Level level, Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(level, entity) ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
    }
}
