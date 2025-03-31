package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record ModelModifyResultEvent(Map<ResourceLocation, BakedModel> models, ModelBakery bakery) {

    public static final EventHelper<ModelModifyResultEvent> EVENT = new EventHelper<>();

}
