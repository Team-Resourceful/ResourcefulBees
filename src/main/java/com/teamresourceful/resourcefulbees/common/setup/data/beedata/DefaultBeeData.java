package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.MutationData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering.RenderData;
import com.teamresourceful.resourcefulbees.common.setup.data.beedata.traits.TraitData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.Map;
import java.util.function.Supplier;

public record DefaultBeeData(Map<Identifier, BeeData<?>> data, Identifier id, MutableComponent displayName, Supplier<EntityType<?>> type) implements com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData {

    public static DefaultBeeData of(Identifier name, Map<Identifier, BeeData<?>> data) {
        return new DefaultBeeData(
            data,
            name,
            Component.translatable("bee_type.resourcefulbees." + name.getPath()),
            Suppliers.memoize(() -> BuiltInRegistries.ENTITY_TYPE.getOptional(name).orElse(EntityTypes.BEE))
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
