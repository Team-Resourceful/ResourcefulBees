package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.records.FloatRange;
import com.teamresourceful.resourcefulbees.common.lib.records.IntRange;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record TradeData(
        Holder<ContextIntProvider> amount,
        Item secondaryItem,
        Holder<ContextIntProvider> secondaryItemCost,
        Holder<ContextFloatProvider> reputationDiscount,
        Holder<ContextIntProvider> maxTrades,
        Holder<ContextIntProvider> xp
) implements BeekeeperTradeData {

    @Override
    public boolean isTradable() {
        return !this.equals(DEFAULT);
    }

    public static final Codec<BeekeeperTradeData> CODEC = RecordCodecBuilder.create(tradeDataInstance -> tradeDataInstance.group(
            ContextIntProviders.CODEC.fieldOf("amount").orElse(ContextIntProviders.exactly(1)).forGetter(BeekeeperTradeData::amount),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("secondaryItem", Items.AIR).forGetter(BeekeeperTradeData::secondaryItem),
            ContextIntProviders.CODEC.fieldOf("secondaryItemCost").orElse(ContextIntProviders.between(1, 4)).forGetter(BeekeeperTradeData::secondaryItemCost),
            FloatRange.codec(0, 1).optionalFieldOf("reputationDiscount", ContextFloatProviders.exactly(0.05f)).forGetter(BeekeeperTradeData::reputationDiscount),
            IntRange.codec(1, 64).optionalFieldOf("maxTrades", ContextIntProviders.exactly(8)).forGetter(BeekeeperTradeData::maxTrades),
            IntRange.codec(1, 64).optionalFieldOf("xp", ContextIntProviders.exactly(3)).forGetter(BeekeeperTradeData::xp)
    ).apply(tradeDataInstance, TradeData::new));

    public static final BeekeeperTradeData DEFAULT = new TradeData(ContextIntProviders.between(0,0), Items.AIR, ContextIntProviders.between(0,0), ContextFloatProviders.exactly(0.05f), ContextIntProviders.exactly(0), ContextIntProviders.exactly(0));

    public static final BeeDataSerializer<BeekeeperTradeData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("trade"), 1, _ -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeekeeperTradeData> serializer() {
        return SERIALIZER;
    }
}
