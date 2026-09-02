package com.teamresourceful.resourcefulbees.common.lib.util;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.utils.GenericMemoryPack;
import com.teamresourceful.resourcefullib.common.utils.neoforge.HiddenGenericMemoryPack;
import it.unimi.dsi.fastutil.booleans.BooleanObjectImmutablePair;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Contract;

public class ModUtils {

    private ModUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static BooleanObjectPair<Vec3> enderEntityTeleport(LivingEntity entity, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, x, y, z);
        return new BooleanObjectImmutablePair<>(NeoForge.EVENT_BUS.post(event).isCanceled(), event.getTarget());
    }

    @Contract(value = "null -> false", pure = true)
    public static boolean isRealPlayer(Player player) {
        return player != null && !(player instanceof FakePlayer);
    }

    public static ResourceKey<? extends Registry<?>> getSpawnDataRegistryKey() {
        return NeoForgeRegistries.Keys.BIOME_MODIFIERS;
    }

    public static Level.ExplosionInteraction getExplosionInteraction(ServerLevel level, Entity entity) {
        return EventHooks.canEntityGrief(level, entity)
                ? Level.ExplosionInteraction.MOB
                : Level.ExplosionInteraction.NONE;
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
