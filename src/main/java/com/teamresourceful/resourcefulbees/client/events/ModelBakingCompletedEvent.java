package com.teamresourceful.resourcefulbees.client.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.client.resources.model.ModelBakery;

public record ModelBakingCompletedEvent(ModelBakery bakery) {

    public static final EventHelper<ModelBakingCompletedEvent> EVENT = new EventHelper<>();

}
