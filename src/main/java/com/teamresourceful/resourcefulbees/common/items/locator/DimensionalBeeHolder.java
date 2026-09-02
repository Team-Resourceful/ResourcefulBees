package com.teamresourceful.resourcefulbees.common.items.locator;

import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.DimensionalBeesPacket;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DimensionalBeeHolder {

    public static final Map<ResourceKey<Level>, List<Identifier>> DIMENSIONAL_BEES = new HashMap<>();

    private DimensionalBeeHolder() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static void updateBees(MinecraftServer server) {
        DIMENSIONAL_BEES.clear();

        for (ServerLevel level : server.getAllLevels()) {
            List<Identifier> bees = level.getChunkSource()
                    .getGenerator()
                    .getBiomeSource()
                    .possibleBiomes()
                    .stream()
                    .filter(Holder::isBound)
                    .map(Holder::value)
                    .map(biome -> biome.getMobSettings().getMobs(ModConstants.RESOURCEFUL_BEE_CATEGORY))
                    .map(WeightedList::unwrap)
                    .flatMap(List::stream)
                    .map(weighted -> weighted.value().type())
                    .filter(CustomBeeEntityType.class::isInstance)
                    .map(CustomBeeEntityType.class::cast)
                    .map(CustomBeeEntityType::getBeeType)
                    .distinct()
                    .toList();

            DIMENSIONAL_BEES.put(level.dimension(), bees);
        }
    }

    public static List<Identifier> getBees(ResourceKey<Level> dimension) {
        return DIMENSIONAL_BEES.getOrDefault(dimension, List.of());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        updateBees(server);
        DimensionalBeesPacket packet = new DimensionalBeesPacket(DIMENSIONAL_BEES);
        event.getRelevantPlayers().forEach(player -> NetworkHandler.NETWORK.sendToPlayer(packet, player));
    }
}