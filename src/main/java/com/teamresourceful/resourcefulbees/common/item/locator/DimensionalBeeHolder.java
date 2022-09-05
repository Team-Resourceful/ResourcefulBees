package com.teamresourceful.resourcefulbees.common.item.locator;

import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntityType;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ServerLevelAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.StructureCheckAccessor;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.server.DimensionalBeesPacket;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DimensionalBeeHolder {

    public static final Map<ResourceKey<Level>, List<String>> DIMENSIONAL_BEES = new HashMap<>();

    private DimensionalBeeHolder() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    private static void updateBees(MinecraftServer server) {
        DIMENSIONAL_BEES.clear();

        for (ServerLevel level : server.getAllLevels()) {
            List<String> bees = new ArrayList<>();

            var generator = ((StructureCheckAccessor) ((ServerLevelAccessor) level).getStructureCheck()).getChunkGenerator();

            generator.getBiomeSource()
                    .possibleBiomes()
                    .stream()
                    .filter(Holder::isBound).map(Holder::get)
                    .map(biome -> biome.getMobSettings().getMobs(ModConstants.BEE_MOB_CATEGORY))
                    .map(WeightedRandomList::unwrap).flatMap(List::stream)
                    .map(data -> data.type).filter(type -> type instanceof CustomBeeEntityType<?>).map(type -> (CustomBeeEntityType<?>)type)
                    .map(CustomBeeEntityType::getBeeType).forEach(bees::add);
            DIMENSIONAL_BEES.put(level.dimension(), bees);
        }
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            updateBees(event.getPlayerList().getServer());
            NetPacketHandler.CHANNEL.sendToPlayers(new DimensionalBeesPacket(DIMENSIONAL_BEES), event.getPlayerList().getPlayers());
        } else {
            if (DIMENSIONAL_BEES.isEmpty()) updateBees(event.getPlayer().server);
            NetPacketHandler.CHANNEL.sendToPlayer(new DimensionalBeesPacket(DIMENSIONAL_BEES), event.getPlayer());
        }
    }
}
