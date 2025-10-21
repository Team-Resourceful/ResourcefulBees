package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.function.ObjIntConsumer;

public record RegisterVillagerTradesEvent(ObjIntConsumer<VillagerTrades.ItemListing> registry, VillagerProfession profession) {

    public static final EventHelper<RegisterVillagerTradesEvent> EVENT = new EventHelper<>();

    public void register(int level, VillagerTrades.ItemListing listing) {
        registry.accept(listing, level);
    }
}
