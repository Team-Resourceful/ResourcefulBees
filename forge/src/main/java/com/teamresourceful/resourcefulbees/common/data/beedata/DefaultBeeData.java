package com.teamresourceful.resourcefulbees.common.data.beedata;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.data.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.data.beedata.rendering.RenderData;
import com.teamresourceful.resourcefulbees.common.data.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.function.Supplier;

public record DefaultBeeData(String name, Map<ResourceLocation, BeeData<?>> data, ResourceLocation id, MutableComponent displayName, Supplier<EntityType<?>> type) implements com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData {

    public static DefaultBeeData of(String name, Map<ResourceLocation, BeeData<?>> data) {
        ResourceLocation id = new ResourceLocation(ModConstants.MOD_ID, name + "_bee");
        return new DefaultBeeData(
            name,
            data,
            id,
            Component.translatable("bee_type.resourcefulbees." + name),
            Suppliers.memoize(() -> Registry.ENTITY_TYPE.getOptional(id).orElse(EntityType.BEE))
        );
    }

    @Override
    public EntityType<?> entityType() {
        return type.get();
    }

    @Override
    public BeeBreedData getBreedData() {
        return getData(BreedData.SERIALIZER);
    }

    @Override
    public BeeCombatData getCombatData() {
        return getData(CombatData.SERIALIZER);
    }

    @Override
    public BeeRenderData getRenderData() {
        return getData(RenderData.SERIALIZER);
    }

    @Override
    public BeeMutationData getMutationData() {
        return getData(MutationData.SERIALIZER);
    }

    @Override
    public BeeCoreData getCoreData() {
        return getData(CoreData.SERIALIZER);
    }

    @Override
    public BeeTraitData getTraitData() {
        return getData(TraitData.SERIALIZER);
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return getData(TradeData.SERIALIZER);
    }
}
