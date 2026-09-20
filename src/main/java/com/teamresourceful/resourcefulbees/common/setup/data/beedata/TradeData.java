package com.teamresourceful.resourcefulbees.common.setup.data.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeDataSerializer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public record TradeData(
        UniformGenerator amount,
        Item secondaryItem,
        UniformGenerator secondaryItemCost,
        float reputationDiscount,
        int maxTrades,
        int xp
) implements BeekeeperTradeData {

    @Override
    public boolean isTradable() {
        return !this.equals(DEFAULT);
    }

    public static final Codec<BeekeeperTradeData> CODEC = RecordCodecBuilder.create(tradeDataInstance -> tradeDataInstance.group(
            UniformGenerator.MAP_CODEC.fieldOf("amount").orElse(UniformGenerator.between(1f,1f)).forGetter(BeekeeperTradeData::amount),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("secondaryItem", Items.AIR).forGetter(BeekeeperTradeData::secondaryItem),
            UniformGenerator.MAP_CODEC.fieldOf("secondaryItemCost").orElse(UniformGenerator.between(1, 4)).forGetter(BeekeeperTradeData::secondaryItemCost),
            Codec.floatRange(0, 1).optionalFieldOf("reputationDiscount", 0.05f).forGetter(BeekeeperTradeData::reputationDiscount),
            Codec.intRange(1, 64).optionalFieldOf("maxTrades", 8).forGetter(BeekeeperTradeData::maxTrades),
            Codec.intRange(1, 64).optionalFieldOf("xp", 3).forGetter(BeekeeperTradeData::xp)
    ).apply(tradeDataInstance, TradeData::new));

    public static final BeekeeperTradeData DEFAULT = new TradeData(UniformGenerator.between(0,0), Items.AIR, UniformGenerator.between(0,0), 0, 0, 0);

    public static final BeeDataSerializer<BeekeeperTradeData> SERIALIZER = BeeDataSerializer.of(ModIdentifier.of("trade"), 1, _ -> CODEC, DEFAULT);

    @Override
    public BeeDataSerializer<BeekeeperTradeData> serializer() {
        return SERIALIZER;
    }
}
