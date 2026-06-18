package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.events.SpawnBabyEvent;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import com.teamresourceful.resourcefullib.common.utils.neoforge.HiddenGenericMemoryPack;
import it.unimi.dsi.fastutil.booleans.BooleanObjectImmutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Contract;

public class ModUtils {

    public static void spawnBabyEvent(SpawnBabyEvent event) {
        SpawnBabyEvent.EVENT.fire(event);
        final BabyEntitySpawnEvent forgeEvent = new BabyEntitySpawnEvent(event.parent1(), event.parent2(), event.getChild());
        NeoForge.EVENT_BUS.post(forgeEvent);
        if (forgeEvent.isCanceled()) {
            event.setCanceled(true);
        }
        event.setChild(forgeEvent.getChild());
    }

    public static BooleanObjectPair<Vec3> enderEntityTeleport(LivingEntity entity, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, x, y, z);
        return new BooleanObjectImmutablePair<>(NeoForge.EVENT_BUS.post(event).isCanceled(), new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isRealPlayer(Player player) {
        return player != null && !(player instanceof FakePlayer);
    }

    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        return NeoForgeRegistries.Keys.BIOME_MODIFIERS;
    }

    public static Level.ExplosionInteraction getExplosionInteraction(Level level, Entity entity) {
        return Level.ExplosionInteraction.NONE; //todo fix this since mob griefing event doesnt exist
        //.getMobGriefingEvent(level, entity) ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
    }

    public static Fluid getFluid(LiquidBlock block) {
        return block.fluid;
    }

    public static GenericMemoryPack createHiddenDataPack(String id, PackMetadataSection meta) {
        return new HiddenGenericMemoryPack(PackType.SERVER_DATA, id, meta) {
            // I don't know why I made HiddenGenericMemoryPack protected.
        };
    }

    @Contract(pure = true)
    public static boolean isProduction() {
        return FMLLoader.getCurrent().isProduction();
    }
}
