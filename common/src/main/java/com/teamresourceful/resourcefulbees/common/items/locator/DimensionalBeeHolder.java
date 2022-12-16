package com.teamresourceful.resourcefulbees.common.items.locator;

import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.DimensionalBeesPacket;
import com.teamresourceful.resourcefulbees.mixin.common.ServerLevelAccessor;
import com.teamresourceful.resourcefulbees.mixin.common.StructureCheckAccessor;
import com.teamresourceful.resourcefulbees.platform.common.events.SyncedDatapackEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DimensionalBeeHolder {

    public static final Map<ResourceKey<Level>, List<String>> DIMENSIONAL_BEES = new HashMap<>();

    private DimensionalBeeHolder() {
        throw new UtilityClassError();
    }

    private static void updateBees(MinecraftServer server) {
        DIMENSIONAL_BEES.clear();

        for (ServerLevel level : server.getAllLevels()) {
            var generator = ((StructureCheckAccessor) ((ServerLevelAccessor) level).getStructureCheck()).getChunkGenerator();

            DIMENSIONAL_BEES.put(level.dimension(), generator.getBiomeSource()
                .possibleBiomes()
                .stream()
                .filter(Holder::isBound).map(Holder::value)
                .map(biome -> biome.getMobSettings().getMobs(ModConstants.BEE_CATEGORY))
                .map(WeightedRandomList::unwrap).flatMap(List::stream)
                .map(data -> data.type)
                .filter(type -> type instanceof CustomBeeEntityType<?>)
                .map(type -> (CustomBeeEntityType<?>)type)
                .map(CustomBeeEntityType::getBeeType)
                .toList());
        }
    }

    public static void onDatapackSync(SyncedDatapackEvent event) {
        if (event.player() == null) {
            updateBees(event.allPlayers().getServer());
            NetworkHandler.CHANNEL.sendToPlayers(new DimensionalBeesPacket(DIMENSIONAL_BEES), event.allPlayers().getPlayers());
        } else {
            if (DIMENSIONAL_BEES.isEmpty()) updateBees(event.player().server);
            NetworkHandler.CHANNEL.sendToPlayer(new DimensionalBeesPacket(DIMENSIONAL_BEES), event.player());
        }
    }
}
