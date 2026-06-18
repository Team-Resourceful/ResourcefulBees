package com.teamresourceful.resourcefulbees.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.world.level.ItemLike;

import java.util.function.ObjIntConsumer;

public record RegisterBurnablesEvent(ObjIntConsumer<ItemLike> register) {

    public static final EventHelper<RegisterBurnablesEvent> EVENT = new EventHelper<>();

    public void register(int time, ItemLike... items) {
        for (ItemLike item : items) {
            register.accept(item, time);
        }
    }
}
